# 📋 Setup Guide — GitHub Upload

## Prerequisites
- GitHub account (free)
- Git installed on your computer
- Android Studio (optional, for building from source)

---

## 🚀 Upload to GitHub

### Option 1: Using GitHub Web Interface (Easiest)

1. **Create Repository**
   - Go to [github.com/new](https://github.com/new)
   - Repository name: `PayloadSender` (or your choice)
   - Description: "Android payload injection app"
   - Make it **Public** (so others can download APK)
   - Click **Create repository**

2. **Upload Files**
   - On your new repo page, click **"uploading an existing file"**
   - Drag and drop the entire **PayloadSender** folder contents
   - Or click to browse and select files
   - Commit message: "Initial commit: PayloadSender app"
   - Click **Commit changes**

3. **Enable GitHub Actions**
   - Go to **Settings** tab
   - Left menu: **Actions → General**
   - Under "Actions permissions" select **"Allow all actions and reusable workflows"**
   - Scroll down and click **Save**

4. **Watch Workflow Run**
   - Go to **Actions** tab
   - You should see "Build APK" workflow running
   - Wait for ✅ green checkmark (~5 minutes)
   - Click the workflow → scroll to **Artifacts** → download `PayloadSender-debug.apk`

---

### Option 2: Using Git Command Line

```bash
# 1. Initialize git (if not already done)
cd /path/to/PayloadSender
git init
git add .
git commit -m "Initial commit: PayloadSender app"

# 2. Add remote (replace USERNAME)
git remote add origin https://github.com/USERNAME/PayloadSender.git

# 3. Create main branch and push
git branch -M main
git push -u origin main
```

Then enable GitHub Actions as in **Option 1, Step 3** above.

---

## ✅ Verification Checklist

- [ ] Repository created and public
- [ ] All files uploaded (check files list)
- [ ] `.github/workflows/build.yml` exists
- [ ] GitHub Actions enabled in Settings
- [ ] Workflow ran successfully (green ✅)
- [ ] APK available in Artifacts

---

## 📲 Install on Phone

### Via APK File
```bash
adb install PayloadSender-debug.apk
```

### Via Direct Download
1. Download APK from GitHub Actions
2. Transfer to phone
3. Go to **Settings → Security → Unknown Sources** (or **Install unknown apps**)
4. Tap APK file to install
5. Grant permissions if prompted

---

## 🔄 After Upload

### Update App
1. Make changes in Android Studio
2. Commit and push: `git add . && git commit -m "message" && git push`
3. Workflow runs automatically
4. New APK available in Actions

### Create Release
1. Go to **Releases** tab
2. Click **Create a new release**
3. Tag: `v1.3` (or version number)
4. Title: `PayloadSender v1.3`
5. Description: Add feature list
6. Upload APK manually if desired
7. Publish release

---

## 🆘 Troubleshooting

**Q: Workflow fails with "namespace not specified"**
- A: Make sure `gradle.properties` is uploaded

**Q: Can't find APK in Artifacts**
- A: Check Actions tab → workflow → Artifacts section (not in files)

**Q: Icon doesn't appear on phone**
- A: Clear app cache or reinstall

**Q: Repository is private**
- A: Go to Settings → Visibility → Change to Public

---

**🎉 You're all set! Happy coding!**
