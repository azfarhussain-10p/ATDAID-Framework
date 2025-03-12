# Product Management Feature

## Overview

The Product Management feature provides a RESTful API for managing products in the system. It includes endpoints for creating, retrieving, and listing products, with appropriate authentication and authorization controls.

## API Endpoints

### Create Product

- **URL**: `/api/products`
- **Method**: `POST`
- **Authentication**: Required (Admin only)
- **Request Body**:
  ```json
  {
    "name": "Product Name",
    "description": "Product Description",
    "price": 99.99,
    "category": "Product Category",
    "inStock": true
  }
  ```
- **Response**: 
  - Status: 201 Created
  - Body: The created product with an auto-generated ID

### Get Product by ID

- **URL**: `/api/products/{id}`
- **Method**: `GET`
- **Authentication**: Not required
- **Response**: 
  - Status: 200 OK
  - Body: The product with the specified ID

### Get All Products

- **URL**: `/api/products`
- **Method**: `GET`
- **Authentication**: Not required
- **Response**: 
  - Status: 200 OK
  - Body: Array of all products

## Authentication

The Product Management API uses JWT-based authentication. To create products, users must have admin privileges, which are verified through the JWT token provided in the Authorization header.

## Testing

The Product Management feature is tested using both TestNG and JUnit:

- **Unit Tests**: `ProductManagementTest` (TestNG)
- **Integration Tests**: `ProductManagementIntegrationTest` (JUnit)

### Running Tests

To run all tests:
```bash
mvn test
```

To run only the Product Management tests:
```bash
mvn test -Dtest=ProductManagementTest,ProductManagementIntegrationTest
```

## Implementation Details

The Product Management feature is implemented using the following components:

- **ProductController**: REST controller that handles HTTP requests
- **Product**: Model class representing a product
- **JwtService**: Service for JWT token validation and user role extraction
- **UserService**: Service for user management and authentication

## Future Enhancements

- Add product update functionality
- Add product deletion functionality
- Implement product search and filtering
- Add pagination for product listing
- Implement product categories and tags