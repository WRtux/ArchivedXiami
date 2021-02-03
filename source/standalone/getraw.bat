@echo off
set /p T=Type: 
set /p L=List: 
if not exist "%L%" exit
if not exist "%T%" mkdir "%T%"
for /f %%I in (%L%) do (
echo Get %T% %%I
curl -x http://proxy.example.com:8888 -A "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko" -m 20 --retry 2 https://www.xiami.com/%T%/%%I >%T%\%%I.html
)
pause
