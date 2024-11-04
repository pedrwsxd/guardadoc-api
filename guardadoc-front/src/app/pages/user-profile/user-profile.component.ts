import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css',
})
export class UserProfileComponent implements OnInit {
  user: User | undefined;
  constructor(private userService: UserService) {}
  ngOnInit(): void {
    const userId = '1'; // Substitua pelo ID do usuário real
    this.userService.getUser(userId).subscribe((data) => {
      this.user = data;
    });
  }
  updateUser(): void {
    if (this.user) {
      this.userService
        .updateUser(this.user.id, this.user)
        .subscribe((updatedUser) => {
          console.log('User updated successfully', updatedUser);
        });
    }
  }
}
