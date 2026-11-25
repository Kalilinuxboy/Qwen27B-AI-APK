# Qwen AI Android App (প্রাথমিক Android Studio project)

এটি Qwen Notebook-এর FastAPI ওয়েব UI-কে Android WebView-এর মধ্যে চালানোর একটি Android app source project। এটি এখনো compiled APK নয়।

## ব্যবহার
1. Android Studio-তে এই ফোল্ডারটি Open করুন।
2. Gradle sync সম্পন্ন করুন।
3. Android SDK 35 ইনস্টল থাকতে হবে।
4. ফোনে Run করুন অথবা Build > Build APK(s) ব্যবহার করুন।
5. Kaggle Notebook-এর সব cell চালিয়ে শেষ cell থেকে পাওয়া সম্পূর্ণ `https://....trycloudflare.com/?k=...` লিংক অ্যাপে পেস্ট করুন।

## গুরুত্বপূর্ণ সীমাবদ্ধতা
- Kaggle Free GPU-তে সার্ভার সবসময় চালু থাকবে না; session শেষ হলে APK AI-তে সংযোগ হারাবে।
- Notebook-এ T4 x2 চাওয়া হয়েছে; Free account-এ এই accelerator সবসময় পাওয়া নিশ্চিত নয়।
- APK-তে Kaggle GPU বা Qwen 27B মডেল অন্তর্ভুক্ত নয়; APK হলো client app।
- Kaggle tunnel URL ও access key ব্যক্তিগত রাখুন।
- এটি পরীক্ষামূলক starter project; প্রকাশের আগে security, API limits, privacy এবং backend uptime শক্ত করতে হবে।
