## install

	# we only need fonts-liberation (yagni)
	apt-get -y install fonts-liberation
	
	# right now not necessary
	apt-get -y install fonts-liberation ttf-mscorefonts-installer

## list

	fc-list
	
	ls -l /usr/share/fonts
	ls -l /usr/local/share/fonts

## and contrib packages for ttf-mscorefonts-installer
   
    # install vi on docker
    apt-get update && apt-get install -y vim
   
    # and contrib packages
	vi /etc/apt/sources.list
	
	deb http://deb.debian.org/debian sid main
	# sid contrib non-free
	deb http://http.us.debian.org/debian sid main contrib non-free
	deb http://security.debian.org sid/updates main contrib non-free

	# not working .. sid/updates Release' does not have a Release file.
	deb http://deb.debian.org/debian-security/ sid/updates main contrib non-free	
	deb http://deb.debian.org/debian sid-updates main contrib non-free
	
	# update new packages
	apt-get update
	
	# TODO for docker
	FROM debian:sid as pdf-converter
	ADD sources.list /etc/apt/sources.list
   
## fontconfig
Font Directories, Matcher, Caching

	more /etc/fonts/fonts.conf
	
	<dir>/usr/share/fonts</dir>
	<dir>/usr/local/share/fonts</dir>

## links
[debian Fonts](https://wiki.debian.org/Fonts)  
[debian SourcesList](https://wiki.debian.org/SourcesList)  
[working sources.list contrib](https://gist.github.com/ChristopherA/680b4eeeeb6e9e4c7fc59c010a23b6cd)  
