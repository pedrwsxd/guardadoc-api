import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DocumentService } from '../../services/document.service';

@Component({
  selector: 'app-document-detail',
  standalone: true,
  imports: [],
  templateUrl: './document-detail.component.html',
  styleUrl: './document-detail.component.css',
})
export class DocumentDetailComponent implements OnInit {
  document: Document | undefined;
  qrCodeUrl: string | undefined;
  constructor(
    private route: ActivatedRoute,
    private documentService: DocumentService
  ) {}
  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.documentService.getDocument(id).subscribe((data) => {
      this.document = data;
      this.loadQRCode(id);
    });
  }
  loadQRCode(id: string): void {
    this.documentService.getQRCode(id).subscribe((blob) => {
      const url = window.URL.createObjectURL(blob);
      this.qrCodeUrl = url;
    });
  }
}
