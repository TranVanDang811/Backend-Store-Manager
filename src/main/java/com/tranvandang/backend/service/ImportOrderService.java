package com.tranvandang.backend.service;

import com.tranvandang.backend.dto.request.CreateImportOrderRequest;
import com.tranvandang.backend.dto.request.ImportOrderDetailRequest;
import com.tranvandang.backend.dto.request.UpdateImportOrderRequest;
import com.tranvandang.backend.dto.response.ImportOrderResponse;
import com.tranvandang.backend.dto.response.ImportOrderStatisticResponse;
import com.tranvandang.backend.entity.ImportOrder;
import com.tranvandang.backend.entity.ImportOrderDetail;
import com.tranvandang.backend.entity.Product;
import com.tranvandang.backend.entity.Supplier;
import com.tranvandang.backend.exception.AppException;
import com.tranvandang.backend.exception.ErrorCode;
import com.tranvandang.backend.mapper.ImportOrderMapper;
import com.tranvandang.backend.repository.ImportOrderRepository;
import com.tranvandang.backend.repository.ProductRepository;
import com.tranvandang.backend.repository.SupplierRepository;
import com.tranvandang.backend.util.ImportStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportOrderService {

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final ImportOrderRepository importOrderRepository;
    private final ImportOrderMapper importOrderMapper;

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    @Transactional
    public ImportOrderResponse createImportOrder(CreateImportOrderRequest request) {
        try {
            // Find the supplier
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND,request.getSupplierId()));

            // Create the import order
            ImportOrder order = new ImportOrder();
            order.setImportDate(LocalDateTime.now());
            order.setNote(request.getNote());
            order.setStatus(ImportStatus.NOT_CONFIRMED);
            order.setSupplier(supplier);

            // Prepare details and calculate total
            List<ImportOrderDetail> details = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;

            for (ImportOrderDetailRequest d : request.getImportDetails()) {
                Product product = productRepository.findById(d.getProductId())
                        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED,d.getProductId()));

                ImportOrderDetail detail = new ImportOrderDetail();
                detail.setImportOrder(order);
                detail.setProduct(product);
                detail.setQuantity(d.getQuantity());
                detail.setImportPrice(d.getImportPrice());

                BigDecimal lineTotal = d.getImportPrice().multiply(BigDecimal.valueOf(d.getQuantity()));
                total = total.add(lineTotal);
                details.add(detail);
            }

            order.setImportDetails(details);
            order.setTotalPrice(total);

            // Save the order
            ImportOrder savedOrder = importOrderRepository.save(order);

            // Load full order with details (to ensure correct mapping)
            ImportOrder fullOrder = importOrderRepository.findByIdWithDetails(savedOrder.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

            return importOrderMapper.toResponse(fullOrder);

        } catch (Exception e) {
            log.error("Unexpected error while creating import order", e);
            throw e;
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    @Transactional
    public ImportOrderResponse updateImportOrder(String orderId, UpdateImportOrderRequest request) {
        ImportOrder order = importOrderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.IMPORT_ORDER_NOT_FOUND, orderId));

        if (order.getStatus() != ImportStatus.NOT_CONFIRMED) {
            throw new IllegalStateException("Only NOT_CONFIRMED orders can be updated.");
        }

        // Cập nhật ghi chú
        order.setNote(request.getNote());
        order.setImportUpdateDate(LocalDateTime.now());
        // Tính lại total
        BigDecimal total = BigDecimal.ZERO;

        // Xoá chi tiết cũ nhưng giữ lại list gốc (rất quan trọng)
        order.getImportDetails().clear();

        // Thêm lại từng chi tiết mới vào list gốc
        for (ImportOrderDetailRequest d : request.getImportDetails()) {
            Product product = productRepository.findById(d.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED, d.getProductId()));

            ImportOrderDetail detail = new ImportOrderDetail();
            detail.setImportOrder(order);
            detail.setProduct(product);
            detail.setQuantity(d.getQuantity());
            detail.setImportPrice(d.getImportPrice());

            total = total.add(d.getImportPrice().multiply(BigDecimal.valueOf(d.getQuantity())));

            order.getImportDetails().add(detail); // 👈 dùng list gốc
        }

        order.setTotalPrice(total);

        ImportOrder saved = importOrderRepository.save(order);
        return importOrderMapper.toResponse(saved);
    }

    public ImportOrderStatisticResponse getStatistics(LocalDate from, LocalDate to) {
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(23, 59, 59);

        Object[] result = (Object[]) importOrderRepository.getStatisticsBetween(fromDateTime, toDateTime);

        long totalOrders = ((Number) result[0]).longValue();
        long totalProducts = ((Number) result[1]).longValue();
        BigDecimal totalAmount = (BigDecimal) result[2];

        return new ImportOrderStatisticResponse(totalOrders, totalProducts, totalAmount);
    }




    @Transactional
    public void confirmImportOrder(String orderId) {
        ImportOrder order = importOrderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.IMPORT_ORDER_NOT_FOUND ));

        if (order.getStatus() != ImportStatus.NOT_CONFIRMED) {
            throw new IllegalStateException("Only DRAFT orders can be confirmed.");
        }

        for (ImportOrderDetail detail : order.getImportDetails()) {
            Product product = detail.getProduct();
            int quantity = detail.getQuantity();

            // Cập nhật tồn kho
            product.setQuantity(product.getQuantity() + quantity);
            productRepository.save(product);
        }

        order.setStatus(ImportStatus.IMPORTED);
        importOrderRepository.save(order);
    }


    public Page<ImportOrderResponse> getImportOrders(
            String status,
            String sortByImportDate,
            String supplierName,
            int page,
            int size) {

        ImportStatus importStatus = null;
        if (status != null && !status.isEmpty()) {
            importStatus = ImportStatus.valueOf(status.toUpperCase());
        }

        Sort sort = Sort.by("importDate");
        sort = "DESC".equalsIgnoreCase(sortByImportDate) ? sort.descending() : sort.ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ImportOrder> ordersPage;

        if (importStatus != null && supplierName != null && !supplierName.isEmpty()) {
            ordersPage = importOrderRepository.findByStatusAndSupplier_NameContainingIgnoreCase(
                    importStatus, supplierName, pageable);
        } else if (importStatus != null) {
            ordersPage = importOrderRepository.findByStatus(importStatus, pageable);
        } else if (supplierName != null && !supplierName.isEmpty()) {
            ordersPage = importOrderRepository.findBySupplier_NameContainingIgnoreCase(supplierName, pageable);
        } else {
            ordersPage = importOrderRepository.findAll(pageable);
        }

        return ordersPage.map(importOrderMapper::toResponse);
    }


    public ImportOrderResponse getById(String id) {
        ImportOrder order = importOrderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMPORT_ORDER_NOT_FOUND ));
        return importOrderMapper.toResponse(order);
    }

    @Transactional
    public void deleteImportOrder(String orderId) {
        ImportOrder order = importOrderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.IMPORT_ORDER_NOT_FOUND));

        if (order.getStatus() == ImportStatus.IMPORTED) {
            throw new IllegalStateException("Cannot delete imported orders");
        }

        importOrderRepository.delete(order);
    }

}

