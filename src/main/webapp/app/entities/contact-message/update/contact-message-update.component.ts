import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IContactMessage } from '../contact-message.model';
import { ContactMessageService } from '../service/contact-message.service';
import { ContactMessageFormGroup, ContactMessageFormService } from './contact-message-form.service';

@Component({
  selector: 'jhi-contact-message-update',
  templateUrl: './contact-message-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ContactMessageUpdateComponent implements OnInit {
  isSaving = false;
  contactMessage: IContactMessage | null = null;

  protected contactMessageService = inject(ContactMessageService);
  protected contactMessageFormService = inject(ContactMessageFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ContactMessageFormGroup = this.contactMessageFormService.createContactMessageFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ contactMessage }) => {
      this.contactMessage = contactMessage;
      if (contactMessage) {
        this.updateForm(contactMessage);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const contactMessage = this.contactMessageFormService.getContactMessage(this.editForm);
    if (contactMessage.id !== null) {
      this.subscribeToSaveResponse(this.contactMessageService.update(contactMessage));
    } else {
      this.subscribeToSaveResponse(this.contactMessageService.create(contactMessage));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IContactMessage>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(contactMessage: IContactMessage): void {
    this.contactMessage = contactMessage;
    this.contactMessageFormService.resetForm(this.editForm, contactMessage);
  }
}
