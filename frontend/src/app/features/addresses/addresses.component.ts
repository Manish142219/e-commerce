import { Component, OnInit } from '@angular/core';
import { AddressService } from '../../core/services/address.service';
import { Address, CreateAddressRequest } from '../../core/models/address.model';

@Component({
  selector: 'app-addresses',
  templateUrl: './addresses.component.html',
  styleUrls: ['./addresses.component.css']
})
export class AddressesComponent implements OnInit {
  addresses: Address[] = [];
  loading = true;
  showForm = false;
  formError = '';
  newAddress: CreateAddressRequest = {
    name: '', phone: '', pincode: '', addressLine: '', city: '', state: '', label: 'HOME', isDefault: false
  };

  constructor(private addressService: AddressService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.addressService.getAddresses().subscribe({
      next: (res) => {
        if (res.success) this.addresses = res.data || [];
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  save(): void {
    this.formError = '';
    this.addressService.createAddress(this.newAddress).subscribe({
      next: (res) => {
        if (res.success) {
          this.showForm = false;
          this.load();
        } else {
          this.formError = res.message || 'Failed to save';
        }
      },
      error: (err) => this.formError = err.error?.message || 'Failed to save'
    });
  }

  setDefault(id: number): void {
    this.addressService.setDefaultAddress(id).subscribe(() => this.load());
  }

  remove(id: number): void {
    this.addressService.deleteAddress(id).subscribe(() => this.load());
  }
}
