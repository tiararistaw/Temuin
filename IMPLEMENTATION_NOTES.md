# Temuin Android - Prototype Flow Alignment

This is the native Kotlin + Jetpack Compose project. The Figma Make React/TypeScript prototype is used as the behavioral and content reference. The existing Temuin V1 Compose theme and feature components are retained.

## Changes in this revision

### Report history and progress (priority)
- Replaced the static `dummyReports` list in `HistoryScreen` with reports from the shared `ReportViewModel` / `ReportRepository` state.
- Report history now filters the current user's reports by `Semua`, `Barang Hilang`, and `Barang Ditemukan`, and displays each report's real ID, date, type, status, and image when available.
- Every report-history card opens the progress page for that exact report ID. This removes the previous broken route where static IDs did not exist in the report repository.
- Report publication success now opens `Riwayat Laporan` directly rather than landing on the `Riwayat Klaim` tab.
- Added report lifecycle statuses: `Aktif`, `Ada Pengajuan Klaim`, `Menunggu Serah Terima`, and `Selesai`.
- Added a linked demo report `FND-8825` and incoming claim `CLM-2044` so the report-history progress flow has a real matching report/claim ID.
- The report progress page now reads the incoming claim from `ClaimViewModel`, supports approve/reject with a rejection reason, updates the report status, confirms handover, and completes the related report.
- After completion, the report progress page links to the existing Rating form. It displays `Ulasan Terkirim` after a review for that report is submitted, preventing the same report-progress CTA from repeatedly inviting a duplicate review.
- Claimant-side receipt confirmation also marks the linked report as completed.
- Own-claim history and incoming-claim state are stored separately in `ClaimViewModel`, so opening report progress does not overwrite the user's claim-history list.

### Home and navigation
- Added a `Progres aktivitasmu` section on Home using the user's active reports; each row opens the corresponding report-progress page.
- Home avatar opens Profile.
- The Home greeting/name now reads from the same profile state used by Profile.
- Main bottom navigation is limited to the main tabs, avoiding overlap with form, detail, progress, and review screens.
- The History route accepts an optional tab argument so publication success can open the report tab while the bottom navigation still opens the default claim tab.

### Profile and reviews
- Updated the single demo profile to match the active demo account (`user-01`, Trisha Malina), rather than showing a different user's identity.
- Existing review screens, review cards, review models, repositories, and components were retained. The existing received-review sample data for `user-01` now identifies the reviewed user as Trisha Malina.
- Review creation continues to use the existing `ReviewFormScreen` and review repository.

## Existing components retained

No original feature source folders or review components were intentionally removed. The `auth`, `claim`, `favorite`, `history`, `home`, `login`, `profile`, `report`, `review`, `navigation`, and `ui/theme` source files remain in the project. Existing review UI files under the original packages are preserved.

## Current limitations

- Repositories are still in-memory dummy repositories. Changes are shared across screens during the app session but are not persistent after the app process is cleared.
- Supabase has not been connected in this build.
- The prototype uses richer browser-side dummy interactions and remote sample images; the Android project keeps the native Compose design system and uses its existing photo handling.
- Favorite data is still backed by its existing separate dummy repository and is not yet unified with `ReportRepository`.
- The build could not be verified in this execution environment: Gradle Wrapper attempted to download Gradle 9.5.0, but network access to `services.gradle.org` was unavailable (`UnknownHostException`). Run `./gradlew assembleDebug` in Android Studio or an environment with the Gradle distribution and Android SDK installed.
