# All Targets
all: ass3

# Tool invocations
# Executable "ass3" depends on the files print.o and run.o.
ass3: bin/connectionHandler.o  bin/echoClient.o
	@echo 'Building target: client'
	@echo 'Invoking: C++ Linker'
	g++ -o bin/bluffer bin/connectionHandler.o bin/echoClient.o  -L/usr/lib -lboost_filesystem -lboost_thread -lboost_system -lboost_locale
	@echo 'Finished building target: client'
	@echo ' '

# Depends on the source and header files
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ -g -Wall -c -Linclude -o bin/connectionHandler.o  src/connectionHandler.cpp 

# Depends on the source and header files 
bin/echoClient.o: src/echoClient.cpp
	g++ -g -Wall -c -Linclude -o bin/echoClient.o  src/echoClient.cpp

#Clean the build directory
clean: 
	rm -f bin/*