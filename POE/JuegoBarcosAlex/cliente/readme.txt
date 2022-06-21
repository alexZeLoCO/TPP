En el programa cliente se ha hecho una ligera modificación, se ha prescindido del menú
para lanzar eventos porque sólo había dos opciones, de las cuáles sólo una tiene interés:
la opción del tiro al océano del oponente. La otra opción que se incluía era la consulta
del número de barcos en el océano, que no es relevante salvo al final del juego.

Ahora en lugar del Menu, se declara un Scanner que se utilizará para leer las coordenadas.
Al prescindir del menú desaparece la función interfazCliente() y es necesario modificar
el bucle de lanzamiento de eventos (turno y el tiro), que ahora queda más claro.
