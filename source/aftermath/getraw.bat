@echo off
set Proxy=http://proxy.example.com:8888
set Host=203.119.244.126
set Agent=Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko
set /p T=Type: 
set /p L=List: 
if not exist "%L%" exit
if not exist "%T%" mkdir "%T%"
for /f %%I in (%L%) do (
if not exist %T%\%%I.html (
echo Get %T% %%I
curl -x "%Proxy%" -m 20 --retry 2 -A "%Agent%" -H "Host:www.xiami.com" -k https://%Host%/%T%/%%I >%T%\%%I.html
) else (
echo Found %T% %%I
)
)
pause
