package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.SiteSetting} entity.
 */
@Schema(description = "Cài đặt chung của website (chỉ dùng 1 bản ghi)")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SiteSettingDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 150)
    private String siteName;

    @Size(max = 500)
    private String logoUrl;

    @Size(max = 300)
    private String sloganVi;

    @Size(max = 300)
    private String sloganEn;

    @Size(max = 500)
    private String heroSubtitleVi;

    @Size(max = 500)
    private String heroSubtitleEn;

    @Size(max = 500)
    private String heroImageUrl;

    @Size(max = 500)
    private String heroVideoUrl;

    @Lob
    private String heroSlides;

    private Boolean heroVideoEnabled;

    @Size(max = 500)
    private String backgroundMusicUrl;

    @Size(max = 300)
    private String aboutTitleVi;

    @Size(max = 300)
    private String aboutTitleEn;

    @Lob
    private String aboutContentVi;

    @Lob
    private String aboutContentEn;

    @Size(max = 150)
    private String highlight1TitleVi;

    @Size(max = 150)
    private String highlight1TitleEn;

    @Size(max = 500)
    private String highlight1TextVi;

    @Size(max = 500)
    private String highlight1TextEn;

    @Size(max = 150)
    private String highlight2TitleVi;

    @Size(max = 150)
    private String highlight2TitleEn;

    @Size(max = 500)
    private String highlight2TextVi;

    @Size(max = 500)
    private String highlight2TextEn;

    @Size(max = 150)
    private String highlight3TitleVi;

    @Size(max = 150)
    private String highlight3TitleEn;

    @Size(max = 500)
    private String highlight3TextVi;

    @Size(max = 500)
    private String highlight3TextEn;

    @Size(max = 20)
    private String hotline;

    @Size(max = 150)
    private String email;

    @Size(max = 20)
    private String whatsappNumber;

    @Size(max = 20)
    private String zaloNumber;

    @Size(max = 300)
    private String messengerUrl;

    @Size(max = 300)
    private String facebookUrl;

    @Size(max = 300)
    private String instagramUrl;

    @Size(max = 300)
    private String tiktokUrl;

    @Size(max = 300)
    private String youtubeUrl;

    @Size(max = 200)
    private String openingHoursVi;

    @Size(max = 200)
    private String openingHoursEn;

    @Size(max = 300)
    private String metaDescriptionVi;

    @Size(max = 300)
    private String metaDescriptionEn;

    @Lob
    private String policyContentVi;

    @Lob
    private String policyContentEn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getSloganVi() {
        return sloganVi;
    }

    public void setSloganVi(String sloganVi) {
        this.sloganVi = sloganVi;
    }

    public String getSloganEn() {
        return sloganEn;
    }

    public void setSloganEn(String sloganEn) {
        this.sloganEn = sloganEn;
    }

    public String getHeroSubtitleVi() {
        return heroSubtitleVi;
    }

    public void setHeroSubtitleVi(String heroSubtitleVi) {
        this.heroSubtitleVi = heroSubtitleVi;
    }

    public String getHeroSubtitleEn() {
        return heroSubtitleEn;
    }

    public void setHeroSubtitleEn(String heroSubtitleEn) {
        this.heroSubtitleEn = heroSubtitleEn;
    }

    public String getHeroImageUrl() {
        return heroImageUrl;
    }

    public void setHeroImageUrl(String heroImageUrl) {
        this.heroImageUrl = heroImageUrl;
    }

    public String getHeroVideoUrl() {
        return heroVideoUrl;
    }

    public void setHeroVideoUrl(String heroVideoUrl) {
        this.heroVideoUrl = heroVideoUrl;
    }

    public String getHeroSlides() {
        return heroSlides;
    }

    public void setHeroSlides(String heroSlides) {
        this.heroSlides = heroSlides;
    }

    public Boolean getHeroVideoEnabled() {
        return heroVideoEnabled;
    }

    public void setHeroVideoEnabled(Boolean heroVideoEnabled) {
        this.heroVideoEnabled = heroVideoEnabled;
    }

    public String getBackgroundMusicUrl() {
        return backgroundMusicUrl;
    }

    public void setBackgroundMusicUrl(String backgroundMusicUrl) {
        this.backgroundMusicUrl = backgroundMusicUrl;
    }

    public String getAboutTitleVi() {
        return aboutTitleVi;
    }

    public void setAboutTitleVi(String aboutTitleVi) {
        this.aboutTitleVi = aboutTitleVi;
    }

    public String getAboutTitleEn() {
        return aboutTitleEn;
    }

    public void setAboutTitleEn(String aboutTitleEn) {
        this.aboutTitleEn = aboutTitleEn;
    }

    public String getAboutContentVi() {
        return aboutContentVi;
    }

    public void setAboutContentVi(String aboutContentVi) {
        this.aboutContentVi = aboutContentVi;
    }

    public String getAboutContentEn() {
        return aboutContentEn;
    }

    public void setAboutContentEn(String aboutContentEn) {
        this.aboutContentEn = aboutContentEn;
    }

    public String getHighlight1TitleVi() {
        return highlight1TitleVi;
    }

    public void setHighlight1TitleVi(String highlight1TitleVi) {
        this.highlight1TitleVi = highlight1TitleVi;
    }

    public String getHighlight1TitleEn() {
        return highlight1TitleEn;
    }

    public void setHighlight1TitleEn(String highlight1TitleEn) {
        this.highlight1TitleEn = highlight1TitleEn;
    }

    public String getHighlight1TextVi() {
        return highlight1TextVi;
    }

    public void setHighlight1TextVi(String highlight1TextVi) {
        this.highlight1TextVi = highlight1TextVi;
    }

    public String getHighlight1TextEn() {
        return highlight1TextEn;
    }

    public void setHighlight1TextEn(String highlight1TextEn) {
        this.highlight1TextEn = highlight1TextEn;
    }

    public String getHighlight2TitleVi() {
        return highlight2TitleVi;
    }

    public void setHighlight2TitleVi(String highlight2TitleVi) {
        this.highlight2TitleVi = highlight2TitleVi;
    }

    public String getHighlight2TitleEn() {
        return highlight2TitleEn;
    }

    public void setHighlight2TitleEn(String highlight2TitleEn) {
        this.highlight2TitleEn = highlight2TitleEn;
    }

    public String getHighlight2TextVi() {
        return highlight2TextVi;
    }

    public void setHighlight2TextVi(String highlight2TextVi) {
        this.highlight2TextVi = highlight2TextVi;
    }

    public String getHighlight2TextEn() {
        return highlight2TextEn;
    }

    public void setHighlight2TextEn(String highlight2TextEn) {
        this.highlight2TextEn = highlight2TextEn;
    }

    public String getHighlight3TitleVi() {
        return highlight3TitleVi;
    }

    public void setHighlight3TitleVi(String highlight3TitleVi) {
        this.highlight3TitleVi = highlight3TitleVi;
    }

    public String getHighlight3TitleEn() {
        return highlight3TitleEn;
    }

    public void setHighlight3TitleEn(String highlight3TitleEn) {
        this.highlight3TitleEn = highlight3TitleEn;
    }

    public String getHighlight3TextVi() {
        return highlight3TextVi;
    }

    public void setHighlight3TextVi(String highlight3TextVi) {
        this.highlight3TextVi = highlight3TextVi;
    }

    public String getHighlight3TextEn() {
        return highlight3TextEn;
    }

    public void setHighlight3TextEn(String highlight3TextEn) {
        this.highlight3TextEn = highlight3TextEn;
    }

    public String getHotline() {
        return hotline;
    }

    public void setHotline(String hotline) {
        this.hotline = hotline;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWhatsappNumber() {
        return whatsappNumber;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public String getZaloNumber() {
        return zaloNumber;
    }

    public void setZaloNumber(String zaloNumber) {
        this.zaloNumber = zaloNumber;
    }

    public String getMessengerUrl() {
        return messengerUrl;
    }

    public void setMessengerUrl(String messengerUrl) {
        this.messengerUrl = messengerUrl;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public String getTiktokUrl() {
        return tiktokUrl;
    }

    public void setTiktokUrl(String tiktokUrl) {
        this.tiktokUrl = tiktokUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getOpeningHoursVi() {
        return openingHoursVi;
    }

    public void setOpeningHoursVi(String openingHoursVi) {
        this.openingHoursVi = openingHoursVi;
    }

    public String getOpeningHoursEn() {
        return openingHoursEn;
    }

    public void setOpeningHoursEn(String openingHoursEn) {
        this.openingHoursEn = openingHoursEn;
    }

    public String getMetaDescriptionVi() {
        return metaDescriptionVi;
    }

    public void setMetaDescriptionVi(String metaDescriptionVi) {
        this.metaDescriptionVi = metaDescriptionVi;
    }

    public String getMetaDescriptionEn() {
        return metaDescriptionEn;
    }

    public void setMetaDescriptionEn(String metaDescriptionEn) {
        this.metaDescriptionEn = metaDescriptionEn;
    }

    public String getPolicyContentVi() {
        return policyContentVi;
    }

    public void setPolicyContentVi(String policyContentVi) {
        this.policyContentVi = policyContentVi;
    }

    public String getPolicyContentEn() {
        return policyContentEn;
    }

    public void setPolicyContentEn(String policyContentEn) {
        this.policyContentEn = policyContentEn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SiteSettingDTO)) {
            return false;
        }

        SiteSettingDTO siteSettingDTO = (SiteSettingDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, siteSettingDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SiteSettingDTO{" +
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
