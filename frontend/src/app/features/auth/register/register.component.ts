import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  registerForm: FormGroup;
  errorMessage = '';
  loading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      phone: ['']
    });
  }

  onSubmit(): void {
    if (this.registerForm.invalid) return;
    this.loading = true;
    this.errorMessage = '';

    this.authService.register(this.registerForm.value).subscribe({
      next: (res) => {
        if (res.success) {
          this.router.navigate(['/home']);
        } else {
          this.errorMessage = res.message || 'Registration failed';
        }
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Registration failed';
        this.loading = false;
      }
    });
  }
}
