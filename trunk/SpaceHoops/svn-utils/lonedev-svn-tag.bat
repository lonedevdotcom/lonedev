REM This batch file is not for public use. Just for me (Richard) to use to stamp versions of my projects.

set USERNAME=<your_yahoo_email_address>
set PASSWORD=<google_code_password>
set GOOGLE_PROJECT=lonedev
set NETBEANS_PROJECT=SpaceHoops
set REVISION=0.02

svn mkdir https://%GOOGLE_PROJECT%.googlecode.com/svn/tags/release-%REVISION% --username %USERNAME% --password %PASSWORD% -m "Release %REVISION%"

svn copy https://%GOOGLE_PROJECT%.googlecode.com/svn/trunk/%NETBEANS_PROJECT% https://%GOOGLE_PROJECT%.googlecode.com/svn/tags/release-%REVISION%/%NETBEANS_PROJECT% --username %USERNAME% --password %PASSWORD% -m "Release %REVISION%"
