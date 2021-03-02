@echo off
set Agent=Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko
set /p L=List: 
if not exist "%L%" exit
for /f "tokens=1,2 delims= " %%E in (%L%) do (
if not exist media\%%F (
echo Get %%E
mkdir media\%%F
rmdir media\%%F
curl -m 10 --retry 2 -A "%Agent%" https:%%E >media\%%F
) else (
echo Found %%F
)
)
pause
