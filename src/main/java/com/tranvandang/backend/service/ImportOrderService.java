package com.tranvandang.backend.service;

import com.tranvandang.backend.dto.request.CreateImportOrderRequest;
import com.tranvandang.backend.dto.request.ImportOrderDetailRequest;
import com.tranvandang.backend.dto.response.ImportOrderResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportOrderService {

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final ImportOrderRepository importOrderRepository;
    private final ImportOrderMapper importOrderMapper;

    @Transactional
    public ImportOrderResponse createImportOrder(CreateImportOrderRequest request) {
        try {
            // Find the supplier
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + request.getSupplierId()));

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
                        .orElseThrow(() -> new RuntimeException("Product not found with ID: " + d.getProductId()));

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
                    .orElseThrow(() -> new RuntimeException("Saved order not found"));

            return importOrderMapper.toResponse(fullOrder);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
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


    public Page<ImportOrderResponse> getImportOrders(String status, String sortByImportDate, int page, int size) {
        ImportStatus importStatus = null;
        if (status != null) {
            importStatus = ImportStatus.valueOf(status.toUpperCase());
        }

        Sort sort = Sort.by("importDate");
        if ("DESC".equalsIgnoreCase(sortByImportDate)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending(); // mặc định ASC
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ImportOrder> ordersPage;
        if (importStatus != null) {
            ordersPage = importOrderRepository.findByStatus(importStatus, pageable);
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

