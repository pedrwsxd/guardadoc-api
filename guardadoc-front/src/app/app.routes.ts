import { Routes } from '@angular/router';
import { DocumentListComponent } from './pages/document-list/document-list.component';
import { DocumentUploadComponent } from './pages/document-upload/document-upload.component';
import { DocumentDetailComponent } from './pages/document-detail/document-detail.component';
import { UserProfileComponent } from './pages/user-profile/user-profile.component';
import { LoginComponent } from './pages/login/login.component';
import { SignUpComponent } from './pages/signup/signup.component';
import { AuthGuard } from './services/auth-guard.service';

export const routes: Routes = [
  {
    path: 'documents',
    component: DocumentListComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'upload',
    component: DocumentUploadComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'documents/:id',
    component: DocumentDetailComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'user-profile',
    component: UserProfileComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'signup',
    component: SignUpComponent,
  },
  { path: '', redirectTo: '/user-profile', pathMatch: 'full' },
];
