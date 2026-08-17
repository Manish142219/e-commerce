export interface Category {
  id: number;
  name: string;
  slug: string;
  imageUrl: string;
  discountText: string;
  parentNav: string;
  variantType?: string;
}
