# Nagare API

Backend he thong quan tri lu hanh Nagare (Cong ty TNHH Du lich Nagare Viet Nhat).
Java 21 · Spring Boot 3.5 · MongoDB · JWT · Cloudinary · Apache POI.

Ban thiet ke day du: `../Document/khung-he-thong-nagare-v1.1.html` (doc muc 01-11 truoc khi sua code,
dac biet muc 11 - nhat ky 31 loi logic da tim va sua).

## Chay local

1. Can MongoDB (local hoac Atlas). Nhanh nhat: `docker run -d -p 27017:27017 --name nagare-mongo mongo:7`.
2. Copy `src/main/resources/application-local.yml` neu can chinh sua (bootstrap admin, jwt secret, mongo uri).
3. Chay:
   ```
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```
4. Dang nhap bang tai khoan Giam doc khoi tao (`app.bootstrap-admin-user` / `app.bootstrap-admin-password`
   trong `application-local.yml`, mac dinh `admin` / `ChangeMe123!`). Lan dang nhap dau se bi bat doi mat khau
   (`mustChangePassword=true`).
5. Swagger UI: http://localhost:8080/swagger-ui.html
6. Health check: http://localhost:8080/actuator/health

## Build

```
./mvnw clean package
```

Jar chay duoc nam o `target/nagare-api.jar`. Can Internet cho lan build dau tien de Maven tai dependency
ve `~/.m2`.

### Ghi chu build tren Windows (quan trong neu duong dan du an co ky tu Unicode)

May build dung de tao repo nay co duong dan chua ky tu tieng Viet co dau
(`D:\Du an cua Anh Thu\BE`). Tren mot so cai dat JDK/Windows, viec truyen duong dan
nay lam tham so `-classpath` cho `java.exe` bi giai ma sai (do khac codepage), khien
Maven bao loi `ClassNotFoundException` du file jar hoan toan hop le. Neu gap loi nay:

- Chay lenh build tu **PowerShell** (khong phai Git Bash/MSYS - MSYS chuyen doi tham so
  dong lenh sang Windows theo cach khac gay loi nang hon), **hoac**
- Tao mot o dia ao tro toi thu muc du an bang ky tu ASCII thuan:
  ```powershell
  subst X: "D:\Du an cua Anh Thu\BE"
  cd X:\
  .\mvnw.cmd clean package
  subst X: /d   # go bo o dia ao khi xong
  ```

Day la han che cua may build cu the, khong phai loi cua ma nguon hay pom.xml.

## Docker

```
docker build -t nagare-api .
docker run -p 8080:8080 --env-file .env nagare-api
```

Dockerfile build nhieu tang (JDK de build, JRE alpine de chay), `JAVA_TOOL_OPTIONS` gioi han RAM
phu hop goi mien phi 512MB cua Render (muc 09 ban thiet ke).

## Bien moi truong bat buoc khi trien khai that

| Bien | Y nghia |
|---|---|
| `MONGODB_URI` | Chuoi ket noi MongoDB Atlas |
| `JWT_SECRET` | Khoa ky JWT, toi thieu 32 byte |
| `CLOUDINARY_URL` | Chuoi ket noi Cloudinary (`cloudinary://key:secret@cloud_name`) |
| `ALLOWED_ORIGINS` | Danh sach domain FE duoc phep goi API, phan cach boi dau phay - KHONG bao gio dung `*` |
| `TZ` | Mui gio, mac dinh `Asia/Ho_Chi_Minh` |
| `COOKIE_DOMAIN` | Domain goc dung chung giua FE/BE (vd `nagare.vn`) |
| `BOOTSTRAP_ADMIN_USER` / `BOOTSTRAP_ADMIN_PASSWORD` | Tai khoan Giam doc khoi tao, chi dung khi DB rong |
| `PORT` | Cong lang nghe (Render tu dat) |

## Cau truc package (muc 03 ban thiet ke)

`com.nagare.{common,identity,hr,catalog,scheduling,sales,operations,documents,reporting,importing}`
- chia theo nghiep vu, khong chia theo tang ky thuat.

## Da implement

- **18 collection MongoDB** (muc 05): users, employees, customers, tours, departures, addOnServices,
  bookings, tourRequests, assignments, tourLogs, tourFeedback, visaCases, leaveRequests, attendance,
  notifications, auditLogs, refreshTokens, counters.
- **Xac thuc**: JWT access 15 phut + refresh 14 ngay (collection `refreshTokens`, thu hoi duoc),
  `tokenVersion` de khoa tai khoan co hieu luc ngay, bootstrap Giam doc dau tien tu bien moi truong,
  `mustChangePassword` chan moi API qua filter.
- **4 luong nghiep vu loi** (muc 06), gom ca 3 nhanh bi bo sot o ban 1.0: sua danh sach khach (transaction
  dieu chinh ghe), huy don da xac nhan (tra seatsConfirmed + REFUND), canh bao doan duoi minPax.
- **7 quy uoc bat buoc trong CLAUDE.md**: khong luu balance/paidAmount (tinh khi doc), dung paxId/docId/
  addOnId thay vi vi tri mang, quyen "cua minh" nam trong Criteria Mongo, `@PreAuthorize` tren moi
  endpoint theo dung ma tran phan quyen muc 04, `assignments` la nguon su that duy nhat, khong luu file
  vao Mongo (chi luu publicId Cloudinary), 3 thao tac hold/pax-update/confirm dung MongoDB
  `@Transactional` that (`MongoTransactionManager`).
- **Diem tinh vi khac**: dem ghe theo `occupiesSeat`, khong tu gop khach theo phone (gan nhan "nghi trung"),
  han visa = MAX(30 ngay truoc khoi hanh, 7 ngay tu hom nay) (xem ghi chu sua loi trong
  `VisaDeadlineCalculator`), chan trung lich HDV ca hai chieu (luc tao assignment va luc duyet nghi phep),
  roster gioi han truong giay to trong khoang ±3 ngay quanh chuyen di, publication tour tach rieng vi/ja
  voi rang buoc Inbound phai co ban Nhat, counters sinh ma bang `findAndModify` nguyen tu, rate limit
  in-memory cho `/api/auth/register` va `/api/public/tour-requests`, CORS doc tu `ALLOWED_ORIGINS`,
  `GlobalExceptionHandler` tra loi dung dinh dang `{ code, message, fieldErrors[] }`, 3 `@Scheduled` job
  (nha cho qua han, canh bao minPax, don refresh token), Swagger UI, Actuator health.
- **DataInitializer**: nap 17 tour mau (7 Outbound, 4 Inbound, 6 Domestic) va 5 dich vu dac quyen muc 7.3
  ho so nang luc khi database rong.
- **Import Excel** (Apache POI) cho tour va khach hang cu, che do preview truoc khi ghi.
- **Test don vi** cho phan nhay cam nhat: tinh cho trong / dem ghe, thanh toan hai chieu, transaction
  giu-xac nhan-huy don (Mockito), chan trung lich HDV ca hai chieu, han nop visa, khoa tai khoan qua
  tokenVersion (JwtAuthenticationFilter), cua so hien thi giay to trong roster.

## Chua lam / gia dinh don gian hoa (co ghi ro trong code, bao lai nguoi dung)

- 6 cau hoi nghiep vu chua chot o CLAUDE.md (gia mua vu, don vi tien Yen/VND, hoa hong, chinh sach hoan
  huy, hoa don VAT, cham cong theo ngay/gio) - cac cho lien quan (vd so tien hoan khi huy don) tam de
  nhan vien nhap tay, dung gia dinh don gian nhat nhu CLAUDE.md yeu cau.
  - Frontend chua duoc dung trong pham vi task nay (chi backend).
- `attendance` tu dong danh dau ON_TOUR tu `assignments` (mo ta trong muc 05) moi co check-in thu cong
  qua API; co the bo sung mot job dong bo tu dong o giai doan sau.
- Chu ky `Cloudinary.privateDownload` dung theo API cua `cloudinary-http44`; can kiem tra lai voi tai
  khoan Cloudinary that truoc khi dung production vi thu vien nay it tai lieu cho luong "authenticated
  delivery" ky han.
- Chua co integration test dung MongoDB that (Testcontainers/Flapdoodle) do moi truong build co the
  offline luc chay CI; toan bo test hien tai la unit test / Mockito khong can DB that, tach rieng logic
  nhay cam ra khoi repository de test duoc doc lap.
