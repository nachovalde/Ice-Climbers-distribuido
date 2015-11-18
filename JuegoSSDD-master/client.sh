
if [ -z "$1" ]
  then echo "Debe ingresar la IP del servidor donde conectarse."
  exit
fi
# Compilación de archivos java
echo "Compilando programa."
javac -cp ".:jar/sigar.jar" src/cl/uchile/dcc/cc5303/*.java
# Creación de directorio para archivos binarios de java
mkdir -p bin/cl/uchile/dcc/cc5303/
mv src/cl/uchile/dcc/cc5303/*.class bin/cl/uchile/dcc/cc5303/
# Correr el cliente
echo "Iniciando Juego"
cd bin
java -Djava.rmi.server.hostname=$1 cl.uchile.dcc.cc5303.Client $1
