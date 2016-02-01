all: clean build

clean:
	rm -rf *.html *.js *.css package-list com/

build:
	git clone https://github.com/coshx/drekkar.git src
	cd src; ./gradlew javadoc
	cp -a src/javadoc/. .
	rm -rf src