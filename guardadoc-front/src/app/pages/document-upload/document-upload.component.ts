import { Component } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { DocumentService } from '../../services/document.service';

@Component({
  selector: 'app-document-upload',
  standalone: true,
  imports: [],
  templateUrl: './document-upload.component.html',
  styleUrl: './document-upload.component.css',
})
export class DocumentUploadComponent {
  uploadForm: FormGroup;
  constructor(
    private formBuilder: FormBuilder,
    private documentService: DocumentService
  ) {
    this.uploadForm = this.formBuilder.group({ file: [''], usuarioId: [''] });
  }
  onFileSelected(event: any) {
    const file = event.target.files[0];
    this.uploadForm.patchValue({ file: file });
  }
  onSubmit() {
    const formData = new FormData();
    formData.append('file', this.uploadForm.get('file')!.value);
    formData.append('usuarioId', this.uploadForm.get('usuarioId')!.value);
    this.documentService.uploadDocument(formData).subscribe((response) => {
      console.log('Upload successful', response);
    });
  }
}
