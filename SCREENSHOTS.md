# DentalCare App - Screen Descriptions & UI Overview

Since this is a code-only repository without actual device screenshots, this document describes the UI and visual design of each screen in the app.

## 📱 App Screens Overview

### 🎨 Design Language

**Material Design 3** with:
- **Primary Color:** Professional Blue (#1565C0)
- **Accent Color:** Teal (#00897B)
- **Corner Radius:** 16dp for cards, 12dp for buttons
- **Elevation:** Subtle shadows (2dp-4dp)
- **Typography:** Roboto font family
- **Icons:** Material Icons Extended
- **Spacing:** 8dp grid system

---

## 1. 🌟 Splash Screen

**Design:**
- Full-screen gradient background (blue to teal)
- Animated dental logo/icon in center
- App name "DentalCare" below logo
- Fade-in animation (500ms)
- 2-second display duration

**Flow:**
- Checks authentication status
- If authenticated → Dashboard
- If not authenticated → Login

---

## 2. 🔐 Login Screen

**Layout:**
- Top section (40%): Gradient background with app branding
- Bottom section (60%): White card with rounded top corners

**Components:**
- **Header:** "Welcome Back" title with subtitle
- **Email Field:** Outlined text field with email validation
- **Password Field:** Outlined text field with visibility toggle
- **Remember Me:** Checkbox with label
- **Forgot Password:** Text button (opens reset dialog)
- **Login Button:** Full-width, rounded corners (12dp), primary color
- **Register Link:** "Don't have an account? Register" text button
- **Loading:** Circular progress indicator overlays button

**Validation:**
- Real-time email format validation
- Password minimum 6 characters
- Error messages below fields in red

---

## 3. 📝 Register Screen

**Layout:**
- Similar to Login with scrollable form

**Components:**
- **First Name:** Required text field
- **Last Name:** Required text field
- **Email:** With validation
- **Password:** With strength indicator and visibility toggle
- **Confirm Password:** Match validation
- **Role Dropdown:** ExposedDropdownMenuBox with 4 roles:
  - Admin
  - Dentist
  - Secretary
  - Assistant
- **Register Button:** Full-width primary button
- **Login Link:** "Already have an account? Login"

**Features:**
- Real-time validation feedback
- Password strength indicator (weak/medium/strong)
- Confirmation before submitting

---

## 4. 🏠 Dashboard Screen

**Header:**
- Top app bar with app name
- Settings icon (top right)
- Personalized greeting: "Good morning, Dr. Smith" with role badge

**Statistics Cards (2x2 Grid):**
1. **Today's Appointments**
   - Large number with animation (count-up effect)
   - Icon: Calendar
   - Color: Blue

2. **Total Patients**
   - Total count
   - Icon: People
   - Color: Teal

3. **Pending Appointments**
   - Pending count
   - Icon: Schedule
   - Color: Orange

4. **Completed Treatments**
   - This week's count
   - Icon: Check Circle
   - Color: Green

**Quick Actions (Grid 2x4):**
- Add Patient
- New Appointment
- Add Treatment
- Upload Diagnostic
- View Reports
- Patient List
- Appointment Schedule
- Settings

Each with icon and label, rounded cards with ripple effect

**Today's Appointments:**
- Horizontal scrollable list
- Cards showing: Patient name, time, service type, status chip
- "View All" button at end

**Bottom Navigation:**
- Dashboard, Patients, Appointments, Reports, More

---

## 5. 👥 Patient Management

### Patient List Screen

**Header:**
- Search bar with magnifying glass icon
- Filter icon (opens filter bottom sheet)
- Sort dropdown (Name A-Z, Date Added)

**List:**
- LazyColumn with patient cards
- Each card shows:
  - Avatar with initials (circular, colored background)
  - Full name (bold, 16sp)
  - Contact number
  - Last visit date
  - Chevron right icon

**Floating Action Button (FAB):**
- Primary color, circular
- Plus icon
- Position: Bottom right with 16dp margin
- Opens Add Patient screen

**Empty State:**
- Icon: Person Add outline
- Text: "No patients yet"
- Description: "Add your first patient to get started"
- Add Patient button

---

### Add/Edit Patient Screen

**Layout:**
- Scrollable form with sections

**Personal Information Section:**
- Section header with divider
- First Name (required, red asterisk)
- Last Name (required)
- Contact Number (phone format validation)
- Address (multiline)

**Medical History Section:**
- Expandable card
- Multiline text field (4 lines min)
- Label: "Medical conditions, allergies, medications"

**Dental History Section:**
- Expandable card
- Multiline text field (4 lines min)
- Label: "Previous treatments, dental issues"

**Actions:**
- Save button (bottom app bar, always visible)
- Cancel icon (top bar)
- Unsaved changes confirmation on back press

---

### Patient Detail Screen

**Header:**
- Large avatar with initials
- Patient full name (24sp, bold)
- Contact info with call icon button
- Edit and Delete icons (top bar)

**Tab Layout:**
Three tabs with swipe navigation:

**1. Overview Tab:**
- Info cards with labeled fields
- Personal Information card
- Medical History card (expandable)
- Dental History card (expandable)
- Registration date

**2. Appointments Tab:**
- List of patient's appointments
- Sorted by date (newest first)
- Status color coding
- Empty state: "No appointments yet"

**3. Treatments Tab:**
- List of treatments
- Each showing: Date, type, dentist
- Empty state: "No treatments recorded"

**Quick Actions (FAB Group):**
- Main FAB with sub-actions:
  - Schedule Appointment
  - Add Treatment
  - Upload Diagnostic

---

## 6. 📅 Appointment Management

### Appointments Screen

**Top Section:**
- Date filter chips (horizontal scroll):
  - Today (selected style)
  - This Week
  - This Month
  - All
- Calendar/List view toggle (icon buttons)

**Filters:**
- Status dropdown: All, Pending, Confirmed, Completed, Cancelled
- Search by patient name

**Appointment Cards:**
- Elevated cards with colored left border (status)
- Patient name (bold, 16sp)
- Service type icon + text
- Date and time with icons
- Dentist name with avatar
- Status chip (colored, rounded)
- Walk-in badge if applicable

**Status Colors:**
- Pending: Orange
- Confirmed: Blue
- Completed: Green
- Cancelled: Red

**FAB:** Add Appointment

---

### Add/Edit Appointment Screen

**Form Layout:**

**Patient Selection:**
- ExposedDropdownMenuBox
- Searchable list
- Shows patient name + contact

**Dentist Selection:**
- Dropdown with dentist list
- Avatar + name display

**Service Type:**
- Dropdown with all 9 types:
  1. Oral Prophylaxis (Cleaning)
  2. Tooth Extraction
  3. Tooth Filling/Restoration
  4. Root Canal Therapy
  5. Denture Fabrication
  6. Odontectomy
  7. Orthodontic Treatment
  8. Teeth Whitening
  9. Diagnostic Procedure

**Date & Time:**
- Date picker (Material 3 calendar)
- Time picker (Material 3 clock)
- Conflict check indicator

**Walk-in Toggle:**
- Switch component
- Label: "Walk-in appointment"

**Notes:**
- Multiline text field
- Placeholder: "Additional notes..."

**Actions:**
- Save button
- Double-booking warning dialog if conflict

---

### Appointment Detail Screen

**Header:**
- Large appointment ID
- Status chip with change status menu

**Patient Quick Info Card:**
- Avatar, name, contact
- "View Full Profile" button

**Appointment Details Cards:**
- Service: Icon + type
- Date: Calendar icon + formatted date
- Time: Clock icon + time
- Dentist: Avatar + name
- Status: Current status with history
- Notes: Expandable section
- Walk-in: Badge if applicable

**Action Buttons (Bottom Sheet):**
- Confirm Appointment (Blue)
- Complete Appointment (Green)
- Cancel Appointment (Red)
- Reschedule (Orange)
- Each with confirmation dialog

---

## 7. 🦷 Treatment Records

### Treatment List Screen

**Filters (Top):**
- Patient filter (dropdown)
- Dentist filter (dropdown)
- Treatment type filter (chips)
- Date range picker

**Treatment Cards:**
- Patient name with avatar
- Treatment type (bold, icon)
- Date (formatted)
- Dentist name
- Follow-up indicator (if required)
- Chevron for details

**FAB:** Add Treatment

**Empty State:**
- Icon: Medical bag
- "No treatments recorded"

---

### Add/Edit Treatment Screen

**Form:**
- Patient selector (searchable dropdown)
- Dentist selector (auto-filled if dentist role)
- Treatment type dropdown
- Treatment date picker
- Treatment notes (large multiline, 6 lines min)
- Follow-up required (switch)
- Follow-up date (shown if toggle on)

**Save Button:** Bottom app bar

---

### Treatment Detail Screen

**Header:**
- Treatment type icon + name
- Date performed

**Details Cards:**
- Patient info (with link to profile)
- Dentist info
- Treatment notes (full display)
- Follow-up information (if applicable)
- Created by + timestamp

**Actions:**
- Edit (top bar)
- Delete (top bar, with confirmation)

---

## 8. 📷 Diagnostics

### Diagnostics List Screen

**View Toggle:**
- Grid view (2 columns)
- List view
- Toggle icons (top bar)

**Grid View:**
- Square thumbnail images
- Patient name overlay (bottom)
- Diagnostic type badge (top right)
- Date stamp

**List View:**
- Thumbnail (left)
- Patient name, type, date (right)
- Chevron icon

**Filters:**
- Type dropdown: All, Radiographs, Photos, Casts
- Patient filter
- Date range

**FAB:** Upload Diagnostic

---

### Upload Diagnostic Screen

**Form:**
- Patient selector
- Diagnostic type dropdown:
  - Panoramic Radiograph
  - Periapical Radiograph
  - Extraoral Photo
  - Intraoral Photo
  - Dental Cast

**Image Selection:**
- Large dashed border box
- "Tap to select image" with camera icon
- Image preview after selection
- Remove button on preview

**Notes:**
- Multiline text field

**Upload Button:**
- Shows progress bar during upload
- Success/error feedback

---

### Diagnostic Viewer Screen

**Full-Screen Image:**
- Black background
- Image centered and scaled to fit
- Pinch-to-zoom gesture (1x to 5x)
- Double-tap to zoom in/out
- Pan when zoomed

**Info Overlay (Bottom Sheet):**
- Swipe up to expand
- Patient name + contact
- Diagnostic type
- Date uploaded
- Notes
- Uploaded by

**Actions (Top Bar):**
- Close (back) button
- Share button
- Download button
- Delete button (if authorized)

---

## 9. 📈 Reports Screen

**Report Type Cards:**
1. **Appointment Report**
   - Icon: Calendar
   - Description: "View appointment statistics"

2. **Patient Visit Summary**
   - Icon: People
   - Description: "Patient visit analytics"

3. **Treatment Statistics**
   - Icon: Analytics
   - Description: "Treatment type breakdown"

**Date Range Selection:**
- Start date picker
- End date picker
- Preset ranges: This Week, This Month, This Year

**Generate Button:**
- Primary button
- Opens results screen

**Results Display:**
- Charts (if implemented)
- Lists with counts
- Export/Share options

---

## 10. ⚙️ Settings Screen

**User Profile Section:**
- Large avatar with initials
- Name (from Firebase)
- Email
- Role badge
- Edit profile button

**Preferences:**
- **Theme:** Radio group
  - Light
  - Dark
  - System default (follows device)
- **Notifications:** Switch toggles
  - Appointment reminders
  - New patient alerts
  - System updates

**Account:**
- Change Password (opens dialog)
- Email: Shows current email
- Role: Display only

**About:**
- App version
- About button (opens dialog with app info)
- Credits: "Built for Dra. Josephine B. Lazaro Dental Care"

**Logout:**
- Red button at bottom
- Confirmation dialog:
  - "Are you sure you want to logout?"
  - Cancel / Logout

---

## 🎨 Common UI Patterns

### Dialogs

**Confirmation Dialog:**
- Title (18sp, bold)
- Message (14sp)
- Cancel button (text, left)
- Confirm button (filled, right)
- Optional destructive style (red for delete)

**Date Picker:**
- Material 3 calendar dialog
- Month/Year selector
- Today button
- Cancel / OK buttons

**Time Picker:**
- Material 3 clock dial or input
- AM/PM toggle
- Cancel / OK buttons

### Loading States

**Full-Screen:**
- Centered CircularProgressIndicator
- Loading message below

**Inline:**
- Small spinner in button
- Disabled state with opacity

**Shimmer Effect:**
- For list items while loading
- Pulsing gray rectangles

### Empty States

**Components:**
- Large icon (96dp, gray)
- Primary message (16sp, bold)
- Secondary message (14sp, gray)
- Optional action button

### Error States

**Components:**
- Error icon (red)
- Error message
- Retry button
- Optional detailed error (expandable)

---

## 🎬 Animations

### Screen Transitions
- Slide in/out (horizontal)
- Fade in/out for overlays
- Scale animation for FAB

### Element Animations
- Count-up animation for statistics
- Ripple effect on cards
- Pull-to-refresh indicator
- Swipe gesture feedback
- Button press feedback
- Loading spinner rotation

### State Changes
- Smooth color transitions for status
- Expand/collapse animations
- Fade in for loaded content

---

## 📐 Layout Specifications

### Spacing
- Screen padding: 16dp
- Card margin: 8dp
- Element spacing: 8dp, 16dp, 24dp (small, medium, large)

### Card Dimensions
- Corner radius: 16dp
- Elevation: 2dp (default), 4dp (elevated)
- Padding: 16dp internal

### Text Sizes
- Display: 28sp (screen titles)
- Headline: 24sp (section headers)
- Title: 20sp (card titles)
- Body: 16sp (main content)
- Label: 14sp (labels, captions)
- Caption: 12sp (timestamps, hints)

### Touch Targets
- Minimum: 48dp x 48dp
- Buttons: 56dp height
- FAB: 56dp x 56dp
- IconButtons: 48dp x 48dp

---

## 🌓 Dark Mode Adaptations

All screens support dark mode with:
- Dark background: #121212
- Surface: #1E1E1E
- Elevated surface: #2C2C2C
- Primary color: Lighter blue
- Text: White/off-white
- Icons: White with opacity
- Dividers: White 12% opacity

---

## ♿ Accessibility

- **Content Descriptions:** All icons and images
- **Font Scaling:** Supports system font size
- **Contrast Ratios:** WCAG AA compliant
- **Touch Targets:** Minimum 48dp
- **Screen Reader:** Compatible with TalkBack

---

This UI description serves as a visual guide for developers and stakeholders to understand the app's design and user experience without requiring actual screenshots.
