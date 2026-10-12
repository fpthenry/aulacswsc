package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * Cài đặt chung của website (chỉ dùng 1 bản ghi)
 */
@Entity
@Table(name = "site_setting")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SiteSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 150)
    @Column(name = "site_name", length = 150, nullable = false)
    private String siteName;

    @Size(max = 500)
    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Size(max = 300)
    @Column(name = "slogan_vi", length = 300)
    private String sloganVi;

    @Size(max = 300)
    @Column(name = "slogan_en", length = 300)
    private String sloganEn;

    @Size(max = 500)
    @Column(name = "hero_subtitle_vi", length = 500)
    private String heroSubtitleVi;

    @Size(max = 500)
    @Column(name = "hero_subtitle_en", length = 500)
    private String heroSubtitleEn;

    @Size(max = 500)
    @Column(name = "hero_image_url", length = 500)
    private String heroImageUrl;

    @Size(max = 500)
    @Column(name = "hero_video_url", length = 500)
    private String heroVideoUrl;

    /** Ảnh slideshow đầu trang, mỗi dòng một URL */
    @Lob
    @Column(name = "hero_slides")
    private String heroSlides;

    @Column(name = "hero_video_enabled")
    private Boolean heroVideoEnabled;

    @Size(max = 500)
    @Column(name = "background_music_url", length = 500)
    private String backgroundMusicUrl;

    @Size(max = 300)
    @Column(name = "about_title_vi", length = 300)
    private String aboutTitleVi;

    @Size(max = 300)
    @Column(name = "about_title_en", length = 300)
    private String aboutTitleEn;

    @Lob
    @Column(name = "about_content_vi")
    private String aboutContentVi;

    @Lob
    @Column(name = "about_content_en")
    private String aboutContentEn;

    @Size(max = 150)
    @Column(name = "highlight_1_title_vi", length = 150)
    private String highlight1TitleVi;

    @Size(max = 150)
    @Column(name = "highlight_1_title_en", length = 150)
    private String highlight1TitleEn;

    @Size(max = 500)
    @Column(name = "highlight_1_text_vi", length = 500)
    private String highlight1TextVi;

    @Size(max = 500)
    @Column(name = "highlight_1_text_en", length = 500)
    private String highlight1TextEn;

    @Size(max = 150)
    @Column(name = "highlight_2_title_vi", length = 150)
    private String highlight2TitleVi;

    @Size(max = 150)
    @Column(name = "highlight_2_title_en", length = 150)
    private String highlight2TitleEn;

    @Size(max = 500)
    @Column(name = "highlight_2_text_vi", length = 500)
    private String highlight2TextVi;

    @Size(max = 500)
    @Column(name = "highlight_2_text_en", length = 500)
    private String highlight2TextEn;

    @Size(max = 150)
    @Column(name = "highlight_3_title_vi", length = 150)
    private String highlight3TitleVi;

    @Size(max = 150)
    @Column(name = "highlight_3_title_en", length = 150)
    private String highlight3TitleEn;

    @Size(max = 500)
    @Column(name = "highlight_3_text_vi", length = 500)
    private String highlight3TextVi;

    @Size(max = 500)
    @Column(name = "highlight_3_text_en", length = 500)
    private String highlight3TextEn;

    @Size(max = 20)
    @Column(name = "hotline", length = 20)
    private String hotline;

    @Size(max = 150)
    @Column(name = "email", length = 150)
    private String email;

    @Size(max = 20)
    @Column(name = "whatsapp_number", length = 20)
    private String whatsappNumber;

    @Size(max = 20)
    @Column(name = "zalo_number", length = 20)
    private String zaloNumber;

    @Size(max = 300)
    @Column(name = "messenger_url", length = 300)
    private String messengerUrl;

    @Size(max = 300)
    @Column(name = "facebook_url", length = 300)
    private String facebookUrl;

    @Size(max = 300)
    @Column(name = "instagram_url", length = 300)
    private String instagramUrl;

    @Size(max = 300)
    @Column(name = "tiktok_url", length = 300)
    private String tiktokUrl;

    @Size(max = 300)
    @Column(name = "youtube_url", length = 300)
    private String youtubeUrl;

    @Size(max = 200)
    @Column(name = "opening_hours_vi", length = 200)
    private String openingHoursVi;

    @Size(max = 200)
    @Column(name = "opening_hours_en", length = 200)
    private String openingHoursEn;

    @Size(max = 300)
    @Column(name = "meta_description_vi", length = 300)
    private String metaDescriptionVi;

    @Size(max = 300)
    @Column(name = "meta_description_en", length = 300)
    private String metaDescriptionEn;

    @Lob
    @Column(name = "policy_content_vi")
    private String policyContentVi;

    @Lob
    @Column(name = "policy_content_en")
    private String policyContentEn;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SiteSetting id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSiteName() {
        return this.siteName;
    }

    public SiteSetting siteName(String siteName) {
        this.setSiteName(siteName);
        return this;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getLogoUrl() {
        return this.logoUrl;
    }

    public SiteSetting logoUrl(String logoUrl) {
        this.setLogoUrl(logoUrl);
        return this;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getSloganVi() {
        return this.sloganVi;
    }

    public SiteSetting sloganVi(String sloganVi) {
        this.setSloganVi(sloganVi);
        return this;
    }

    public void setSloganVi(String sloganVi) {
        this.sloganVi = sloganVi;
    }

    public String getSloganEn() {
        return this.sloganEn;
    }

    public SiteSetting sloganEn(String sloganEn) {
        this.setSloganEn(sloganEn);
        return this;
    }

    public void setSloganEn(String sloganEn) {
        this.sloganEn = sloganEn;
    }

    public String getHeroSubtitleVi() {
        return this.heroSubtitleVi;
    }

    public SiteSetting heroSubtitleVi(String heroSubtitleVi) {
        this.setHeroSubtitleVi(heroSubtitleVi);
        return this;
    }

    public void setHeroSubtitleVi(String heroSubtitleVi) {
        this.heroSubtitleVi = heroSubtitleVi;
    }

    public String getHeroSubtitleEn() {
        return this.heroSubtitleEn;
    }

    public SiteSetting heroSubtitleEn(String heroSubtitleEn) {
        this.setHeroSubtitleEn(heroSubtitleEn);
        return this;
    }

    public void setHeroSubtitleEn(String heroSubtitleEn) {
        this.heroSubtitleEn = heroSubtitleEn;
    }

    public String getHeroImageUrl() {
        return this.heroImageUrl;
    }

    public SiteSetting heroImageUrl(String heroImageUrl) {
        this.setHeroImageUrl(heroImageUrl);
        return this;
    }

    public void setHeroImageUrl(String heroImageUrl) {
        this.heroImageUrl = heroImageUrl;
    }

    public String getHeroVideoUrl() {
        return this.heroVideoUrl;
    }

    public SiteSetting heroVideoUrl(String heroVideoUrl) {
        this.setHeroVideoUrl(heroVideoUrl);
        return this;
    }

    public void setHeroVideoUrl(String heroVideoUrl) {
        this.heroVideoUrl = heroVideoUrl;
    }

    public String getHeroSlides() {
        return this.heroSlides;
    }

    public SiteSetting heroSlides(String heroSlides) {
        this.setHeroSlides(heroSlides);
        return this;
    }

    public void setHeroSlides(String heroSlides) {
        this.heroSlides = heroSlides;
    }

    public Boolean getHeroVideoEnabled() {
        return this.heroVideoEnabled;
    }

    public SiteSetting heroVideoEnabled(Boolean heroVideoEnabled) {
        this.setHeroVideoEnabled(heroVideoEnabled);
        return this;
    }

    public void setHeroVideoEnabled(Boolean heroVideoEnabled) {
        this.heroVideoEnabled = heroVideoEnabled;
    }

    public String getBackgroundMusicUrl() {
        return this.backgroundMusicUrl;
    }

    public SiteSetting backgroundMusicUrl(String backgroundMusicUrl) {
        this.setBackgroundMusicUrl(backgroundMusicUrl);
        return this;
    }

    public void setBackgroundMusicUrl(String backgroundMusicUrl) {
        this.backgroundMusicUrl = backgroundMusicUrl;
    }

    public String getAboutTitleVi() {
        return this.aboutTitleVi;
    }

    public SiteSetting aboutTitleVi(String aboutTitleVi) {
        this.setAboutTitleVi(aboutTitleVi);
        return this;
    }

    public void setAboutTitleVi(String aboutTitleVi) {
        this.aboutTitleVi = aboutTitleVi;
    }

    public String getAboutTitleEn() {
        return this.aboutTitleEn;
    }

    public SiteSetting aboutTitleEn(String aboutTitleEn) {
        this.setAboutTitleEn(aboutTitleEn);
        return this;
    }

    public void setAboutTitleEn(String aboutTitleEn) {
        this.aboutTitleEn = aboutTitleEn;
    }

    public String getAboutContentVi() {
        return this.aboutContentVi;
    }

    public SiteSetting aboutContentVi(String aboutContentVi) {
        this.setAboutContentVi(aboutContentVi);
        return this;
    }

    public void setAboutContentVi(String aboutContentVi) {
        this.aboutContentVi = aboutContentVi;
    }

    public String getAboutContentEn() {
        return this.aboutContentEn;
    }

    public SiteSetting aboutContentEn(String aboutContentEn) {
        this.setAboutContentEn(aboutContentEn);
        return this;
    }

    public void setAboutContentEn(String aboutContentEn) {
        this.aboutContentEn = aboutContentEn;
    }

    public String getHighlight1TitleVi() {
        return this.highlight1TitleVi;
    }

    public SiteSetting highlight1TitleVi(String highlight1TitleVi) {
        this.setHighlight1TitleVi(highlight1TitleVi);
        return this;
    }

    public void setHighlight1TitleVi(String highlight1TitleVi) {
        this.highlight1TitleVi = highlight1TitleVi;
    }

    public String getHighlight1TitleEn() {
        return this.highlight1TitleEn;
    }

    public SiteSetting highlight1TitleEn(String highlight1TitleEn) {
        this.setHighlight1TitleEn(highlight1TitleEn);
        return this;
    }

    public void setHighlight1TitleEn(String highlight1TitleEn) {
        this.highlight1TitleEn = highlight1TitleEn;
    }

    public String getHighlight1TextVi() {
        return this.highlight1TextVi;
    }

    public SiteSetting highlight1TextVi(String highlight1TextVi) {
        this.setHighlight1TextVi(highlight1TextVi);
        return this;
    }

    public void setHighlight1TextVi(String highlight1TextVi) {
        this.highlight1TextVi = highlight1TextVi;
    }

    public String getHighlight1TextEn() {
        return this.highlight1TextEn;
    }

    public SiteSetting highlight1TextEn(String highlight1TextEn) {
        this.setHighlight1TextEn(highlight1TextEn);
        return this;
    }

    public void setHighlight1TextEn(String highlight1TextEn) {
        this.highlight1TextEn = highlight1TextEn;
    }

    public String getHighlight2TitleVi() {
        return this.highlight2TitleVi;
    }

    public SiteSetting highlight2TitleVi(String highlight2TitleVi) {
        this.setHighlight2TitleVi(highlight2TitleVi);
        return this;
    }

    public void setHighlight2TitleVi(String highlight2TitleVi) {
        this.highlight2TitleVi = highlight2TitleVi;
    }

    public String getHighlight2TitleEn() {
        return this.highlight2TitleEn;
    }

    public SiteSetting highlight2TitleEn(String highlight2TitleEn) {
        this.setHighlight2TitleEn(highlight2TitleEn);
        return this;
    }

    public void setHighlight2TitleEn(String highlight2TitleEn) {
        this.highlight2TitleEn = highlight2TitleEn;
    }

    public String getHighlight2TextVi() {
        return this.highlight2TextVi;
    }

    public SiteSetting highlight2TextVi(String highlight2TextVi) {
        this.setHighlight2TextVi(highlight2TextVi);
        return this;
    }

    public void setHighlight2TextVi(String highlight2TextVi) {
        this.highlight2TextVi = highlight2TextVi;
    }

    public String getHighlight2TextEn() {
        return this.highlight2TextEn;
    }

    public SiteSetting highlight2TextEn(String highlight2TextEn) {
        this.setHighlight2TextEn(highlight2TextEn);
        return this;
    }

    public void setHighlight2TextEn(String highlight2TextEn) {
        this.highlight2TextEn = highlight2TextEn;
    }

    public String getHighlight3TitleVi() {
        return this.highlight3TitleVi;
    }

    public SiteSetting highlight3TitleVi(String highlight3TitleVi) {
        this.setHighlight3TitleVi(highlight3TitleVi);
        return this;
    }

    public void setHighlight3TitleVi(String highlight3TitleVi) {
        this.highlight3TitleVi = highlight3TitleVi;
    }

    public String getHighlight3TitleEn() {
        return this.highlight3TitleEn;
    }

    public SiteSetting highlight3TitleEn(String highlight3TitleEn) {
        this.setHighlight3TitleEn(highlight3TitleEn);
        return this;
    }

    public void setHighlight3TitleEn(String highlight3TitleEn) {
        this.highlight3TitleEn = highlight3TitleEn;
    }

    public String getHighlight3TextVi() {
        return this.highlight3TextVi;
    }

    public SiteSetting highlight3TextVi(String highlight3TextVi) {
        this.setHighlight3TextVi(highlight3TextVi);
        return this;
    }

    public void setHighlight3TextVi(String highlight3TextVi) {
        this.highlight3TextVi = highlight3TextVi;
    }

    public String getHighlight3TextEn() {
        return this.highlight3TextEn;
    }

    public SiteSetting highlight3TextEn(String highlight3TextEn) {
        this.setHighlight3TextEn(highlight3TextEn);
        return this;
    }

    public void setHighlight3TextEn(String highlight3TextEn) {
        this.highlight3TextEn = highlight3TextEn;
    }

    public String getHotline() {
        return this.hotline;
    }

    public SiteSetting hotline(String hotline) {
        this.setHotline(hotline);
        return this;
    }

    public void setHotline(String hotline) {
        this.hotline = hotline;
    }

    public String getEmail() {
        return this.email;
    }

    public SiteSetting email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWhatsappNumber() {
        return this.whatsappNumber;
    }

    public SiteSetting whatsappNumber(String whatsappNumber) {
        this.setWhatsappNumber(whatsappNumber);
        return this;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public String getZaloNumber() {
        return this.zaloNumber;
    }

    public SiteSetting zaloNumber(String zaloNumber) {
        this.setZaloNumber(zaloNumber);
        return this;
    }

    public void setZaloNumber(String zaloNumber) {
        this.zaloNumber = zaloNumber;
    }

    public String getMessengerUrl() {
        return this.messengerUrl;
    }

    public SiteSetting messengerUrl(String messengerUrl) {
        this.setMessengerUrl(messengerUrl);
        return this;
    }

    public void setMessengerUrl(String messengerUrl) {
        this.messengerUrl = messengerUrl;
    }

    public String getFacebookUrl() {
        return this.facebookUrl;
    }

    public SiteSetting facebookUrl(String facebookUrl) {
        this.setFacebookUrl(facebookUrl);
        return this;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getInstagramUrl() {
        return this.instagramUrl;
    }

    public SiteSetting instagramUrl(String instagramUrl) {
        this.setInstagramUrl(instagramUrl);
        return this;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public String getTiktokUrl() {
        return this.tiktokUrl;
    }

    public SiteSetting tiktokUrl(String tiktokUrl) {
        this.setTiktokUrl(tiktokUrl);
        return this;
    }

    public void setTiktokUrl(String tiktokUrl) {
        this.tiktokUrl = tiktokUrl;
    }

    public String getYoutubeUrl() {
        return this.youtubeUrl;
    }

    public SiteSetting youtubeUrl(String youtubeUrl) {
        this.setYoutubeUrl(youtubeUrl);
        return this;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getOpeningHoursVi() {
        return this.openingHoursVi;
    }

    public SiteSetting openingHoursVi(String openingHoursVi) {
        this.setOpeningHoursVi(openingHoursVi);
        return this;
    }

    public void setOpeningHoursVi(String openingHoursVi) {
        this.openingHoursVi = openingHoursVi;
    }

    public String getOpeningHoursEn() {
        return this.openingHoursEn;
    }

    public SiteSetting openingHoursEn(String openingHoursEn) {
        this.setOpeningHoursEn(openingHoursEn);
        return this;
    }

    public void setOpeningHoursEn(String openingHoursEn) {
        this.openingHoursEn = openingHoursEn;
    }

    public String getMetaDescriptionVi() {
        return this.metaDescriptionVi;
    }

    public SiteSetting metaDescriptionVi(String metaDescriptionVi) {
        this.setMetaDescriptionVi(metaDescriptionVi);
        return this;
    }

    public void setMetaDescriptionVi(String metaDescriptionVi) {
        this.metaDescriptionVi = metaDescriptionVi;
    }

    public String getMetaDescriptionEn() {
        return this.metaDescriptionEn;
    }

    public SiteSetting metaDescriptionEn(String metaDescriptionEn) {
        this.setMetaDescriptionEn(metaDescriptionEn);
        return this;
    }

    public void setMetaDescriptionEn(String metaDescriptionEn) {
        this.metaDescriptionEn = metaDescriptionEn;
    }

    public String getPolicyContentVi() {
        return this.policyContentVi;
    }

    public SiteSetting policyContentVi(String policyContentVi) {
        this.setPolicyContentVi(policyContentVi);
        return this;
    }

    public void setPolicyContentVi(String policyContentVi) {
        this.policyContentVi = policyContentVi;
    }

    public String getPolicyContentEn() {
        return this.policyContentEn;
    }

    public SiteSetting policyContentEn(String policyContentEn) {
        this.setPolicyContentEn(policyContentEn);
        return this;
    }

    public void setPolicyContentEn(String policyContentEn) {
        this.policyContentEn = policyContentEn;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SiteSetting)) {
            return false;
        }
        return getId() != null && getId().equals(((SiteSetting) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SiteSetting{" +
            "id=" + getId() +
            ", siteName='" + getSiteName() + "'" +
            ", logoUrl='" + getLogoUrl() + "'" +
            ", sloganVi='" + getSloganVi() + "'" +
            ", sloganEn='" + getSloganEn() + "'" +
            ", heroSubtitleVi='" + getHeroSubtitleVi() + "'" +
            ", heroSubtitleEn='" + getHeroSubtitleEn() + "'" +
            ", heroImageUrl='" + getHeroImageUrl() + "'" +
            ", heroVideoUrl='" + getHeroVideoUrl() + "'" +
            ", heroVideoEnabled='" + getHeroVideoEnabled() + "'" +
            ", backgroundMusicUrl='" + getBackgroundMusicUrl() + "'" +
            ", aboutTitleVi='" + getAboutTitleVi() + "'" +
            ", aboutTitleEn='" + getAboutTitleEn() + "'" +
            ", aboutContentVi='" + getAboutContentVi() + "'" +
            ", aboutContentEn='" + getAboutContentEn() + "'" +
            ", highlight1TitleVi='" + getHighlight1TitleVi() + "'" +
            ", highlight1TitleEn='" + getHighlight1TitleEn() + "'" +
            ", highlight1TextVi='" + getHighlight1TextVi() + "'" +
            ", highlight1TextEn='" + getHighlight1TextEn() + "'" +
            ", highlight2TitleVi='" + getHighlight2TitleVi() + "'" +
            ", highlight2TitleEn='" + getHighlight2TitleEn() + "'" +
            ", highlight2TextVi='" + getHighlight2TextVi() + "'" +
            ", highlight2TextEn='" + getHighlight2TextEn() + "'" +
            ", highlight3TitleVi='" + getHighlight3TitleVi() + "'" +
            ", highlight3TitleEn='" + getHighlight3TitleEn() + "'" +
            ", highlight3TextVi='" + getHighlight3TextVi() + "'" +
            ", highlight3TextEn='" + getHighlight3TextEn() + "'" +
            ", hotline='" + getHotline() + "'" +
            ", email='" + getEmail() + "'" +
            ", whatsappNumber='" + getWhatsappNumber() + "'" +
            ", zaloNumber='" + getZaloNumber() + "'" +
            ", messengerUrl='" + getMessengerUrl() + "'" +
            ", facebookUrl='" + getFacebookUrl() + "'" +
            ", instagramUrl='" + getInstagramUrl() + "'" +
            ", tiktokUrl='" + getTiktokUrl() + "'" +
            ", youtubeUrl='" + getYoutubeUrl() + "'" +
            ", openingHoursVi='" + getOpeningHoursVi() + "'" +
            ", openingHoursEn='" + getOpeningHoursEn() + "'" +
            ", metaDescriptionVi='" + getMetaDescriptionVi() + "'" +
            ", metaDescriptionEn='" + getMetaDescriptionEn() + "'" +
            ", policyContentVi='" + getPolicyContentVi() + "'" +
            ", policyContentEn='" + getPolicyContentEn() + "'" +
            "}";
    }
}
