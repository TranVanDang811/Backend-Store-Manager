package com.tranvandang.backend.constant;

public enum PredefinedPermission {
    // User
    USER_READ,
    USER_CREATE,
    USER_UPDATE,
    USER_DELETE,

    // Role & permission
    ROLE_ASSIGN,
    PERMISSION_MANAGE,

    // Product
    PRODUCT_CREATE,
    PRODUCT_READ,
    PRODUCT_UPDATE,
    PRODUCT_DELETE,

    // Category
    CATEGORY_CREATE,
    CATEGORY_READ,
    CATEGORY_UPDATE,
    CATEGORY_DELETE,

    // Brand
    BRAND_CREATE,
    BRAND_READ,
    BRAND_UPDATE,
    BRAND_DELETE,

    // Supplier
    SUPPLIER_CREATE,
    SUPPLIER_READ,
    SUPPLIER_UPDATE,
    SUPPLIER_DELETE,

    // Import Order
    IMPORT_ORDER_CREATE,
    IMPORT_ORDER_READ,
    IMPORT_ORDER_UPDATE,

    // Order
    ORDER_CREATE,
    ORDER_READ,
    ORDER_UPDATE,

    // Other
    INVENTORY_READ,
    DASHBOARD_ACCESS,
    PROFILE_UPDATE
}
