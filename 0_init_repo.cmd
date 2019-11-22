@ECHO OFF

set appname=transferapid
set project_name=%appname%-prj


echo "# %project_name%" >> README.md
git init
git add README.md
git commit -m "init-repo"
git remote add origin https://github.com/boobeer/transferapid-prj.git
git push -u origin master