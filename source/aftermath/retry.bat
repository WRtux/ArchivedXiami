@echo off
set Proxy=http://proxy.example.com:8888
set Host=203.119.244.126
set Agent=Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko
set /p T=Type: 
if not exist "%T%" exit
for %%F in (%T%\*.html) do (
if %%~zF lss 30720 (
echo Retry %T% %%~nF
curl -x "%Proxy%" -m 20 --retry 3 -A "%Agent%" -H "Host:www.xiami.com" -k https://%Host%/%T%/%%~nF >%%F
)
)
pause
