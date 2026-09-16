package com.nagare.catalog;

import com.nagare.catalog.model.AddOnService;
import com.nagare.catalog.model.Tour;
import com.nagare.catalog.repo.AddOnServiceRepository;
import com.nagare.catalog.repo.TourRepository;
import com.nagare.common.model.Bilingual;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Nap 17 tour mau va cac dich vu dac quyen (muc 7.3 ho so nang luc) khi database rong.
 * Ban tieng Nhat de placeholder ngan neu chua co ban dich that - van dung cau truc song ngu.
 */
@Component
@Order(10)
public class DataInitializer implements ApplicationRunner {

    private final TourRepository tourRepository;
    private final AddOnServiceRepository addOnServiceRepository;

    public DataInitializer(TourRepository tourRepository, AddOnServiceRepository addOnServiceRepository) {
        this.tourRepository = tourRepository;
        this.addOnServiceRepository = addOnServiceRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (tourRepository.count() == 0) {
            tourRepository.saveAll(sampleTours());
        }
        if (addOnServiceRepository.count() == 0) {
            addOnServiceRepository.saveAll(sampleAddOns());
        }
    }

    private List<Tour> sampleTours() {
        record Sample(String code, String titleVi, String titleJa, Tour.Type type, Tour.Theme theme,
                       int days, int nights, double price, String currency) {}

        List<Sample> samples = List.of(
                new Sample("OB-001", "Nhat Ban mua hoa anh dao", "日本の桜の旅", Tour.Type.OUTBOUND, Tour.Theme.CLASSIC, 6, 5, 32000000, "VND"),
                new Sample("OB-002", "Kyoto - Osaka co kinh", "京都・大阪の旅", Tour.Type.OUTBOUND, Tour.Theme.HERITAGE, 5, 4, 28000000, "VND"),
                new Sample("OB-003", "Hokkaido bang tuyet", "北海道の雪旅", Tour.Type.OUTBOUND, Tour.Theme.NATURE, 6, 5, 36000000, "VND"),
                new Sample("OB-004", "Tokyo hien dai va cong nghe", "東京モダンツアー", Tour.Type.OUTBOUND, Tour.Theme.YOUTH, 5, 4, 27000000, "VND"),
                new Sample("OB-005", "Onsen chua lanh Kyushu", "九州温泉ヒーリング", Tour.Type.OUTBOUND, Tour.Theme.HEALING, 5, 4, 30000000, "VND"),
                new Sample("OB-006", "Kham pha Okinawa bien dao", "沖縄の島旅", Tour.Type.OUTBOUND, Tour.Theme.ADVENTURE, 5, 4, 26000000, "VND"),
                new Sample("OB-007", "Hoc thuat Nhat Ban cho sinh vien", "日本アカデミックツアー", Tour.Type.OUTBOUND, Tour.Theme.ACADEMIC, 7, 6, 34000000, "VND"),
                new Sample("IB-001", "Ha Long - Ninh Binh cho khach Nhat", "ハロン湾・ニンビン", Tour.Type.INBOUND, Tour.Theme.NATURE, 4, 3, 65000, "JPY"),
                new Sample("IB-002", "Sai Gon - Mekong kham pha", "サイゴン・メコン探検", Tour.Type.INBOUND, Tour.Theme.ADVENTURE, 4, 3, 60000, "JPY"),
                new Sample("IB-003", "Ha Noi pho co di san", "ハノイ旧市街遺産", Tour.Type.INBOUND, Tour.Theme.HERITAGE, 3, 2, 45000, "JPY"),
                new Sample("IB-004", "Da Nang - Hoi An nghi duong", "ダナン・ホイアン", Tour.Type.INBOUND, Tour.Theme.HEALING, 5, 4, 72000, "JPY"),
                new Sample("DOM-001", "Sapa mua lua chin", "サパの黄金の稲穂", Tour.Type.DOMESTIC, Tour.Theme.NATURE, 3, 2, 4500000, "VND"),
                new Sample("DOM-002", "Phu Quoc bien dao nghi duong", "フーコック島リゾート", Tour.Type.DOMESTIC, Tour.Theme.HEALING, 4, 3, 7500000, "VND"),
                new Sample("DOM-003", "Da Lat mong mo", "ダラット高原の旅", Tour.Type.DOMESTIC, Tour.Theme.CLASSIC, 3, 2, 3800000, "VND"),
                new Sample("DOM-004", "Hue - Da Nang - Hoi An di san mien Trung", "フエ・ダナン・ホイアン遺産", Tour.Type.DOMESTIC, Tour.Theme.HERITAGE, 5, 4, 8200000, "VND"),
                new Sample("DOM-005", "Con Dao huyen bi", "コンダオ島の神秘", Tour.Type.DOMESTIC, Tour.Theme.ADVENTURE, 3, 2, 6200000, "VND"),
                new Sample("DOM-006", "Tay Bac mua hoa ban", "西北高原バナの花", Tour.Type.DOMESTIC, Tour.Theme.YOUTH, 4, 3, 5200000, "VND")
        );

        return samples.stream().map(s -> {
            Tour t = new Tour();
            t.setCode(s.code());
            t.setType(s.type());
            t.setTheme(s.theme());
            t.setTitle(new Bilingual(s.titleVi(), s.titleJa()));
            t.setSlug(new Bilingual(slugify(s.titleVi()) + "-" + s.code().toLowerCase(),
                    slugify(s.titleVi()) + "-" + s.code().toLowerCase() + "-ja"));
            t.setSummary(new Bilingual(s.titleVi() + " - hanh trinh dac sac cua Nagare.",
                    s.titleJa() + " - Nagareの特別な旅程です。"));
            t.setDurationDays(s.days());
            t.setDurationNights(s.nights());
            t.setBasePriceAdult(s.price());
            t.setCurrency(s.currency());
            // Xuat ban san ban tieng Viet de co du lieu demo; Inbound can them ban Nhat truoc khi publish that.
            t.getPublication().getVi().setStatus(Tour.PubStatus.PUBLISHED);
            t.getPublication().getJa().setStatus(
                    s.type() == Tour.Type.INBOUND ? Tour.PubStatus.PUBLISHED : Tour.PubStatus.DRAFT);
            return t;
        }).toList();
    }

    private List<AddOnService> sampleAddOns() {
        record Sample(String code, String nameVi, String nameJa, AddOnService.Category category, double price, int leadTimeDays) {}
        List<Sample> samples = List.of(
                new Sample("ADDON-CONCERT", "Ve concert", "コンサートチケット", AddOnService.Category.CONCERT, 3500000, 14),
                new Sample("ADDON-KINTSUGI", "Workshop Kintsugi", "金継ぎワークショップ", AddOnService.Category.WORKSHOP, 1200000, 7),
                new Sample("ADDON-KIMONO", "Thue Kimono", "着物レンタル", AddOnService.Category.COSTUME_PHOTO, 800000, 2),
                new Sample("ADDON-ONSEN", "Onsen rieng", "貸切温泉", AddOnService.Category.PRIVATE_ONSEN, 1500000, 3),
                new Sample("ADDON-SIM", "Sim 4G / Wifi", "SIM/Wi-Fiレンタル", AddOnService.Category.SIM_WIFI, 300000, 1)
        );
        return samples.stream().map(s -> {
            AddOnService a = new AddOnService();
            a.setCode(s.code());
            a.setName(new Bilingual(s.nameVi(), s.nameJa()));
            a.setCategory(s.category());
            a.setPricingUnit(AddOnService.PricingUnit.PER_PAX);
            a.setPrice(s.price());
            a.setCurrency("VND");
            a.setLeadTimeDays(s.leadTimeDays());
            a.setRequiresConfirmation(true);
            a.setStatus(AddOnService.Status.ACTIVE);
            return a;
        }).toList();
    }

    private String slugify(String s) {
        return java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase().trim().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }
}
