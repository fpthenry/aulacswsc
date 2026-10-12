import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IGalleryImage } from '../gallery-image.model';

@Component({
  selector: 'jhi-gallery-image-detail',
  templateUrl: './gallery-image-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class GalleryImageDetailComponent {
  galleryImage = input<IGalleryImage | null>(null);

  previousState(): void {
    window.history.back();
  }
}
