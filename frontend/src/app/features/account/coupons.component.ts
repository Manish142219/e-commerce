import { Component } from '@angular/core';

@Component({
  selector: 'app-coupons',
  templateUrl: './coupons.component.html',
  styleUrls: ['./coupons.component.css']
})
export class CouponsComponent {
  coupons = [
    { code: 'SAVE500', desc: 'Get ₹500 off on orders above ₹1999' },
    { code: 'MYNTRA300', desc: 'Get ₹300 off on fashion items' },
    { code: 'FLAT10', desc: 'Flat ₹100 off on any order' }
  ];
}
