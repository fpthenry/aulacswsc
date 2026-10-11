import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'aulacswscApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'service-category',
    data: { pageTitle: 'aulacswscApp.serviceCategory.home.title', authorities: ['ROLE_ADMIN'] },
    loadChildren: () => import('./service-category/service-category.routes'),
  },
  {
    path: 'spa-service',
    data: { pageTitle: 'aulacswscApp.spaService.home.title', authorities: ['ROLE_ADMIN'] },
    loadChildren: () => import('./spa-service/spa-service.routes'),
  },
  {
    path: 'customer',
    data: { pageTitle: 'aulacswscApp.customer.home.title', authorities: ['ROLE_ADMIN', 'ROLE_STAFF'] },
    loadChildren: () => import('./customer/customer.routes'),
  },
  {
    path: 'staff',
    data: { pageTitle: 'aulacswscApp.staff.home.title', authorities: ['ROLE_ADMIN'] },
    loadChildren: () => import('./staff/staff.routes'),
  },
  {
    path: 'booking',
    data: { pageTitle: 'aulacswscApp.booking.home.title', authorities: ['ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_CUSTOMER'] },
    loadChildren: () => import('./booking/booking.routes'),
  },
  {
    path: 'booking-item',
    data: { pageTitle: 'aulacswscApp.bookingItem.home.title', authorities: ['ROLE_ADMIN', 'ROLE_STAFF'] },
    loadChildren: () => import('./booking-item/booking-item.routes'),
  },
  {
    path: 'promotion',
    data: { pageTitle: 'aulacswscApp.promotion.home.title', authorities: ['ROLE_ADMIN'] },
    loadChildren: () => import('./promotion/promotion.routes'),
  },
  {
    path: 'review',
    data: { pageTitle: 'aulacswscApp.review.home.title', authorities: ['ROLE_ADMIN'] },
    loadChildren: () => import('./review/review.routes'),
  },
  {
    path: 'contact-message',
    data: { pageTitle: 'aulacswscApp.contactMessage.home.title', authorities: ['ROLE_ADMIN'] },
    loadChildren: () => import('./contact-message/contact-message.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
