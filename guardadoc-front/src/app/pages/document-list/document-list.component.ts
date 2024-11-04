import { Component, OnInit } from '@angular/core';
import { DocumentService } from '../../services/document.service';

@Component({
  selector: 'app-document-list',
  standalone: true,
  imports: [],
  templateUrl: './document-list.component.html',
  styleUrl: './document-list.component.css'
})
export class DocumentListComponent implements OnInit {
    documents: Document[] = []; 
    constructor(private documentService: DocumentService) { } 
    ngOnInit(): void { this.documentService.getDocuments().subscribe(data => { this.documents = data; }); }
}
