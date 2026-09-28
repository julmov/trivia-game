.PHONY: run compile clean

compile:
	mvn -q compile

run: compile
	mvn -q exec:java

clean:
	@if exist out rmdir /s /q out