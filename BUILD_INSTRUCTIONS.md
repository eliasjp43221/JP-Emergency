# বিল্ড ও রিলিজ নির্দেশিকা (BUILD INSTRUCTIONS)

এই ডকুমেন্টে **জামালপুর সেবা (Jamalpur Sheba)** অ্যান্ড্রয়েড অ্যাপের সোর্স কোড থেকে ডিবাগ ও রিলিজ APK তৈরি, টেস্ট রান এবং গিটহাব রিলিজ প্রকাশের পূর্ণাঙ্গ নিয়মাবলি দেওয়া হয়েছে।

---

## ১. প্রজেক্টের প্রযুক্তিগত সারসংক্ষেপ
- **Application ID:** `com.jamalpur.sheba`
- **Namespace:** `com.example`
- **Minimum SDK:** `24` (Android 7.0)
- **Target / Compile SDK:** `36`
- **Java Version:** JDK 11 / JDK 17 Compatible
- **UI Framework:** Jetpack Compose + Material 3
- **Local Persistence:** Room Database + Jetpack DataStore

---

## ২. ইউনিট ও রোবলেকট্রিক টেস্ট রান করা
প্রজেক্টের সার্চ, ফিল্টার, ডাটাবেস, ইন্টেন্ট ও ইউজার ইন্টারফেস পরীক্ষা করতে নিচের কমান্ডটি রান করুন:

```bash
gradle :app:testDebugUnitTest
```

---

## ৩. ডিবাগ APK তৈরি করা (Debug APK Build)
পরীক্ষামূলক ব্যবহারের জন্য ইনস্টলযোগ্য ডিবাগ APK তৈরি করতে:

```bash
gradle :app:assembleDebug
```
*(অথবা লোকাল মেশিনে Gradle Wrapper থাকলে `./gradlew assembleDebug`)*

- **তৈরিকৃত ডিবাগ APK ফাইলের অবস্থান:**
  `app/build/outputs/apk/debug/app-debug.apk`

---

## ৪. সাইনড রিলিজ APK তৈরি করা (Signed Release APK Configuration)

নিরাপত্তার স্বার্থে কোনো প্রাইভেট কীস্টোর (`my-upload-key.jks`) বা পাসওয়ার্ড গিটহাব রিপোজিটরিতে কমিট করা হয়নি। `app/build.gradle.kts` ফাইলে রিলিজ সাইনিং এনভায়রনমেন্ট ভেরিয়েবলের মাধ্যমে কনফিগার করা আছে:

### ধাপ ক: নতুন রিলিজ কীস্টোর তৈরি (যদি আগে না থাকে)
```bash
keytool -genkey -v -keystore my-upload-key.jks \
  -alias upload -keyalg RSA -keysize 2048 -validity 10000
```
*(সতর্কতা: `.gitignore` ফাইলে `*.jks` যুক্ত আছে, কখনোই কীস্টোর ফাইল গিটে পুশ করবেন না।)*

### ধাপ খ: এনভায়রনমেন্ট ভেরিয়েবল সেট করে রিলিজ বিল্ড রান করা
```bash
export KEYSTORE_PATH="/absolute/path/to/my-upload-key.jks"
export STORE_PASSWORD="your_keystore_password"
export KEY_PASSWORD="your_key_password"

gradle :app:assembleRelease
```

- **প্রত্যাশিত রিলিজ APK অবস্থান:**
  `app/build/outputs/apk/release/app-release.apk`

---

## ৫. GitHub Actions ও প্রথম GitHub Release প্রকাশের ধাপসমূহ

এই প্রজেক্টে দুটি স্বয়ংক্রিয় ওয়ার্কফ্লো যুক্ত করা হয়েছে:
১. `.github/workflows/android-build.yml`: প্রতিটি `push` ও `pull_request`-এ স্বয়ংক্রিয়ভাবে টেস্ট রান করে ও APK আর্টিফ্যাক্ট তৈরি করে।
২. `.github/workflows/release-apk.yml`: `v*.*.*` ফরম্যাটের ট্যাগ পুশ করলে স্বয়ংক্রিয়ভাবে GitHub Release তৈরি করে এবং `.apk` ফাইলটি রিলিজ অ্যাসেট হিসেবে যুক্ত করে।

### গিটহাবে রিপোজিটরি পুশ ও প্রথম রিলিজ (`v1.0.0`) প্রকাশের কমান্ড:

```bash
# ১. গিট ইনিশিয়ালাইজ ও ফাইল যুক্ত করা
git init
git add .
git commit -m "feat: initial production release of Jamalpur Sheba v1.0.0"

# ২. গিটহাব রিমোট রিপোজিটরি যুক্ত করা (আপনার গিটহাব ইউজারনেম বসান)
git branch -M main
git remote add origin https://github.com/<YOUR_GITHUB_USERNAME>/jamalpur-sheba-android.git
git push -u origin main

# ৩. রিলিজ ট্যাগ তৈরি ও পুশ করা (এটি স্বয়ংক্রিয়ভাবে GitHub Release ও APK তৈরি করবে)
git tag -a v1.0.0 -m "জামালপুর সেবা সংস্করণ ১.০.০"
git push origin v1.0.0
```

### ঐচ্ছিক: গিটহাব রিলিজ সাইনিং সিক্রেটস (GitHub Secrets)
আপনি যদি গিটহাব অ্যাকশনসের মাধ্যমে সাইনড রিলিজ APK (`app-release.apk`) প্রকাশ করতে চান, তবে গিটহাব রিপোজিটরির **Settings → Secrets and variables → Actions**-এ নিচের ৩টি সিক্রেট যুক্ত করুন:
- `RELEASE_KEYSTORE_BASE64`: `base64 -w 0 my-upload-key.jks` এর আউটপুট
- `STORE_PASSWORD`: আপনার কীস্টোর পাসওয়ার্ড
- `KEY_PASSWORD`: আপনার কী পাসওয়ার্ড
*(এই সিক্রেটগুলো না থাকলে রিলিজ ওয়ার্কফ্লো স্বয়ংক্রিয়ভাবে ইনস্টলযোগ্য ডিবাগ-সাইনড APK বিল্ড করে রিলিজে সংযুক্ত করবে।)*
