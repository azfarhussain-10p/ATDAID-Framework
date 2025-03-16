# Changelog

All notable changes to the ATDAID Framework will be documented in this file.

## [1.1.0] - 2025-03-16

### Added
- New `forceProcessAll()` method in `BatchProcessor` for immediate log processing
- Logger caching in `BatchProcessor` to improve performance
- Thread safety in batch processing using `ReentrantLock`
- New edge case tests for `ProductService`:
  - Testing products with zero price and quantity
  - Testing products with maximum length values
  - Testing empty search results
  - Testing non-existent product retrieval by SKU
  - Testing empty active product lists

### Changed
- Updated JWT implementation to use modern JJWT approach
- Replaced deprecated `SignatureAlgorithm.HS256` with `Jwts.SIG.HS384`
- Updated method signatures for `signWith` and `verifyWith` in `JwtService`
- Enhanced documentation throughout the codebase with comprehensive Javadoc
- Improved error handling in `BatchProcessor` for queue overflow conditions
- Added default values for configuration parameters in `BatchProcessor`

### Fixed
- Fixed initialization issue in `BatchProcessor` that was causing application context failures
- Fixed builder warning in `ProductRequest` by adding `@Builder.Default` to the `active` field
- Added `@Deprecated` annotation to `LegacyProductController` for consistency

## [1.0.0] - 2025-03-10

### Added
- Initial release of the ATDAID Framework
- Product Management with CRUD operations
- JWT-based authentication and authorization
- Advanced logging system with performance optimization
- Comprehensive test suite with unit, integration, and feature tests 