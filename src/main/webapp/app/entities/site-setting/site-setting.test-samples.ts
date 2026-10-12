import { ISiteSetting, NewSiteSetting } from './site-setting.model';

export const sampleWithRequiredData: ISiteSetting = {
  id: 5534,
  siteName: 'indeed',
};

export const sampleWithPartialData: ISiteSetting = {
  id: 7542,
  siteName: 'without newsletter',
  logoUrl: 'blah considering cautiously',
  sloganEn: 'uh-huh',
  heroSubtitleVi: 'cafe understanding',
  heroSubtitleEn: 'save flat delirious',
  aboutContentEn: '../fake-data/blob/hipster.txt',
  highlight1TitleVi: 'lively retrospectivity notwithstanding',
  highlight2TitleEn: 'bolster above',
  highlight2TextVi: 'outside',
  highlight3TitleVi: 'that upon per',
  highlight3TitleEn: 'reassuringly',
  highlight3TextEn: 'phooey digit',
  email: 'Germaine_Weissnat@hotmail.com',
  messengerUrl: 'boss likewise',
  facebookUrl: 'diagram vary',
  instagramUrl: 'whoever',
  youtubeUrl: 'enchanting cheerfully bitterly',
  openingHoursVi: 'react',
  policyContentEn: '../fake-data/blob/hipster.txt',
};

export const sampleWithFullData: ISiteSetting = {
  id: 14729,
  siteName: 'under duh likewise',
  logoUrl: 'federate as',
  sloganVi: 'treasure sandy fraudster',
  sloganEn: 'oh forgo chairperson',
  heroSubtitleVi: 'lazily repeatedly pivot',
  heroSubtitleEn: 'indeed categorise',
  heroImageUrl: 'unlike closely noxious',
  heroVideoUrl: 'until',
  heroVideoEnabled: true,
  backgroundMusicUrl: 'excepting unexpectedly',
  aboutTitleVi: 'drat duh navigate',
  aboutTitleEn: 'not yummy at',
  aboutContentVi: '../fake-data/blob/hipster.txt',
  aboutContentEn: '../fake-data/blob/hipster.txt',
  highlight1TitleVi: 'finding besides',
  highlight1TitleEn: 'mammoth yippee',
  highlight1TextVi: 'advanced fisherman what',
  highlight1TextEn: 'dense properly as',
  highlight2TitleVi: 'abaft yesterday scarcely',
  highlight2TitleEn: 'aside big follower',
  highlight2TextVi: 'silky ack',
  highlight2TextEn: 'via shore',
  highlight3TitleVi: 'pish and',
  highlight3TitleEn: 'larva entomb',
  highlight3TextVi: 'developmental pfft video',
  highlight3TextEn: 'devastation',
  hotline: 'resort frightened',
  email: 'Allison.Wisozk@gmail.com',
  whatsappNumber: 'tremendously pepper ',
  zaloNumber: 'after doing',
  messengerUrl: 'lest',
  facebookUrl: 'or',
  instagramUrl: 'bulky',
  tiktokUrl: 'meanwhile',
  youtubeUrl: 'than',
  openingHoursVi: 'cool',
  openingHoursEn: 'through who',
  metaDescriptionVi: 'oil rally',
  metaDescriptionEn: 'scotch but',
  policyContentVi: '../fake-data/blob/hipster.txt',
  policyContentEn: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewSiteSetting = {
  siteName: 'attribute',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
