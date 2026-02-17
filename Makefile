run:
	mkdir -p build
	javac --module-path $(PATH_TO_FX) --add-modules javafx.controls src/BusinessApp.java src/Business.java src/BusinessManager.java -d build
	java --module-path $(PATH_TO_FX):src --add-modules javafx.controls -cp build BusinessApp -d build

clean:
	rm -rf build