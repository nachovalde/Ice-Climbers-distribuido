if [ -z "$1" ]
  then echo "Debe ingresar la IP del servidor donde conectarse."
  exit
fi
if [ -z "$2" ]
  then echo "Debe indicar el número de vidas que tendrán los jugadores."
  exit
fi
if [ -z "$3" ]
  then echo "Debe ingresar el número de jugadores que participarán."
  
  exit
fi
# Compilación de archivos java
echo "Compilando programa."
javac src/cl/uchile/dcc/cc5303/*.java
# Creación de directorio para archivos binarios de java
mkdir -p bin/cl/uchile/dcc/cc5303/
mv src/cl/uchile/dcc/cc5303/*.class bin/cl/uchile/dcc/cc5303/
# Reiniciar rmi
echo "Cerrando registros previos de RMI"
fuser -k 1099/tcp
echo "Iniciando RMI"
cd bin
rmiregistry &
# Correr servidor
echo "Iniciando servidor"
if [ -z "$4" ]; then
  echo "server inicial"
  java -Djava.rmi.server.hostname=$1 cl.uchile.dcc.cc5303.Server $1 $2 $3
  exit
else
  java -Djava.rmi.server.hostname=$1 cl.uchile.dcc.cc5303.Server $1 $2 $3 $4
  exit
fi
