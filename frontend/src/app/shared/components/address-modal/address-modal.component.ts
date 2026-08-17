import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { AddressService } from '../../../core/services/address.service';
import { DeliveryService } from '../../../core/services/delivery.service';
import { Address, CreateAddressRequest, DeliveryCheck } from '../../../core/models/address.model';

@Component({
  selector: 'app-address-modal',
  templateUrl: './address-modal.component.html',
  styleUrls: ['./address-modal.component.css']
})
export class AddressModalComponent implements OnInit, OnChanges {
  @Input() show = false;
  @Output() showChange = new EventEmitter<boolean>();
  @Output() addressSelected = new EventEmitter<Address>();
  @Output() deliveryChecked = new EventEmitter<DeliveryCheck>();

  pincodeInput = '';
  addresses: Address[] = [];
  deliveryInfo: DeliveryCheck | null = null;
  checkingPincode = false;
  pincodeError = '';
  loadingAddresses = false;

  showAddForm = false;
  savingAddress = false;
  formError = '';

  newAddress: CreateAddressRequest = {
    name: '',
    phone: '',
    pincode: '',
    addressLine: '',
    city: '',
    state: '',
    label: 'HOME',
    isDefault: true
  };

  constructor(
    private addressService: AddressService,
    private deliveryService: DeliveryService
  ) {}

  ngOnInit(): void {
    this.pincodeInput = this.addressService.getSelectedPincode();
    if (this.show) {
      this.loadAddresses();
      if (this.pincodeInput) {
        this.checkPincode();
      }
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['show']?.currentValue === true) {
      this.loadAddresses();
      this.pincodeInput = this.addressService.getSelectedPincode();
      if (this.pincodeInput) {
        this.checkPincode();
      }
    }
  }

  loadAddresses(): void {
    this.loadingAddresses = true;
    this.addressService.getAddresses().subscribe({
      next: (res) => {
        if (res.success) this.addresses = res.data || [];
        this.loadingAddresses = false;
      },
      error: () => this.loadingAddresses = false
    });
  }

  checkPincode(): void {
    if (!this.pincodeInput || this.pincodeInput.length !== 6) {
      this.pincodeError = 'Enter a valid 6-digit pincode';
      return;
    }

    this.checkingPincode = true;
    this.pincodeError = '';
    this.deliveryService.checkPincode(this.pincodeInput).subscribe({
      next: (res) => {
        if (res.success) {
          this.deliveryInfo = res.data;
          this.addressService.setSelectedPincode(this.pincodeInput);
          this.deliveryChecked.emit(res.data);
          if (!res.data.deliverable) {
            this.pincodeError = res.data.message;
          }
        }
        this.checkingPincode = false;
      },
      error: () => {
        this.pincodeError = 'Failed to check pincode';
        this.checkingPincode = false;
      }
    });
  }

  selectAddress(address: Address): void {
    this.addressService.setSelectedAddress(address);
    this.addressService.setSelectedPincode(address.pincode);
    this.pincodeInput = address.pincode;
    this.checkPincode();
    this.addressSelected.emit(address);
    this.close();
  }

  saveNewAddress(): void {
    this.formError = '';
    if (!this.newAddress.name || !this.newAddress.phone || !this.newAddress.pincode ||
        !this.newAddress.addressLine || !this.newAddress.city || !this.newAddress.state) {
      this.formError = 'Please fill all required fields';
      return;
    }

    this.savingAddress = true;
    this.addressService.createAddress(this.newAddress).subscribe({
      next: (res) => {
        if (res.success) {
          this.addresses.push(res.data);
          this.selectAddress(res.data);
          this.showAddForm = false;
          this.resetForm();
        } else {
          this.formError = res.message || 'Failed to save address';
        }
        this.savingAddress = false;
      },
      error: (err) => {
        this.formError = err.error?.message || 'Failed to save address';
        this.savingAddress = false;
      }
    });
  }

  openAddForm(): void {
    this.showAddForm = true;
    this.newAddress.pincode = this.pincodeInput || '';
  }

  close(): void {
    this.show = false;
    this.showChange.emit(false);
    this.showAddForm = false;
    this.formError = '';
  }

  private resetForm(): void {
    this.newAddress = {
      name: '', phone: '', pincode: '', addressLine: '',
      city: '', state: '', label: 'HOME', isDefault: true
    };
  }
}
