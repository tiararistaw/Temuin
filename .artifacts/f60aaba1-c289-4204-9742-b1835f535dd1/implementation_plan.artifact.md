# Implementation Plan - Ensure Design Consistency Across All Screens

This plan outlines the steps to ensure 100% design consistency across all screens in Temuin (colors, buttons in lazy rows & lazy lists, cards, layout, and headers).

## User Review Required

> [!NOTE]
> We will standardize design tokens (`TemuinTeal`, `TemuinBackground`, `TemuinMuted`, `TemuinDark`, `TemuinLavender`, `TemuinPaleBlue`, `TemuinBorder`), unified filter chips for `LazyRow`, unified card styling (`RoundedCornerShape(16.dp)` / `18.dp`, white background, `1.dp` elevation), unified layout backgrounds (`TemuinBackground`), and unified headers across all screens.

## Proposed Changes

### Shared Components (`review/ui/components/ReviewComponents.kt` or new shared constants/components)
- Add or export a reusable `TemuinFilterChip` and unified `TemuinTopBar` so all screens use identical lazy row button styling and header structure.

### Screen Refactoring for Consistency
- **Home Screen (`HomeScreen.kt`)**: Align colors with `TemuinTeal`, `TemuinDark`, `TemuinBackground`. Ensure cards and lazy row filter chips match the unified system.
- **Claim Screens (`ClaimHistoryScreen.kt`, `ClaimDetailScreen.kt`, `ClaimFormScreen.kt`, `FoundItemDetailScreen.kt`)**: Remove local color definitions (`HistoryTeal`, `DetailTeal`, `ClaimPrimary`, `Teal`) and use `TemuinTeal`, `TemuinBackground`, `TemuinDark`. Unify cards, lazy rows, and headers.
- **History & Search Screens (`SearchHistoryScreens.kt`)**: Align `SearchScreen` and `HistoryScreen` filter lazy rows with `TemuinFilterChip`, unified cards, and layout background.
- **Report & Favorite & Profile Screens**: Ensure all cards, layout padding, headers, and lazy list/row buttons adhere to the Temuin Design System.

## Verification Plan

### Automated Tests
- Run Gradle build (`app:assembleDebug`) to ensure all components and screens compile successfully without syntax or type errors.

### Manual Verification
- Deploy and inspect app screens to verify uniform background, consistent header styles, cards, and lazy row/list button appearance.
