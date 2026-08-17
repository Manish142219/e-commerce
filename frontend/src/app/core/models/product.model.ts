export interface Product {
  id: number;
  brand: string;
  name: string;
  description: string;
  price: number;
  mrp: number;
  discountPercent: number;
  imageUrl: string;
  images: string[];
  sizes: string[];
  colors: string[];
  rating: number;
  ratingCount: number;
  stockQuantity: number;
  categoryId: number;
  categoryName: string;
  categorySlug?: string;
  genderSection: string;
  /** CLOTHING | FOOTWEAR | BEAUTY | ACCESSORY */
  variantType?: string;
  /** SELECT SIZE / SELECT QUANTITY / VOLUME etc. */
  variantLabel?: string;
}
