@echo off
set /p T=Type: 
if not exist "%T%" exit
for %%F in (%T%\*.html) do (
if %%~zF lss 30720 (
echo Retry %T% %%~nF
curl -x http://proxy.example.com:8888 -A "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko" -m 20 --retry 3 https://www.xiami.com/%T%/%%~nF >%%F
)
)
pause
