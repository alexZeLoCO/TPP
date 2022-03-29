;;; *************
;;; * SESIÓN-02 *
;;; *************

(require racket/trace)
(require mzlib/compat)         ; Importa una biblioteca. En particular, nos
                               ; permite utilizar la función atom? que, en
                               ; otro caso, no estaría disponible


;;; Definición de funciones, sintaxis:
;;;
;;; (define (f param1 param2 ...) Sexp)
;;;
;;; Nombre de la función: f
;;; Parámetros de la función: param1 param2 ...    (pueden ser 0)
;;; Cuerpo de la definición: cualquier S-expresión Sexp evaluable
;;; Resultado de la llamada: la evaluación de la S-expresión que resulta
;;; de sustituir los parámetros de la función en la S-expresión Sexp por
;;; argumentos de la llamada.
;;;
;;; Ejemplos:

(define (saluda) '¡Hola_Mundo!) ; definición
(saluda)                        ; invocación (o llamada)

(define (suma a b) (+ a b))     ; definición
(suma 3 -10)                    ; invocación


;;; Definición de funciones recursivas sobre listas
;;; -----------------------------------------------
;;;
;;; En el análisis por casos se utilizará la notación funcional estándar:
;;; <nombre_función>(arg0, arg1, ...). Por ejemplo, para f(l):
;;;
;;; 1. Base       : el resultado de la función la lista vacía;
;;;                 f(()) = lo que corresponda según problema a resolver
;;; 2. Recurrencia: l no es la lista vacía; es decir, l=cons(car(l), cdr(l))
;;;      Hipótesis: se supone conocido f(cdr(l))=H
;;;          Tesis: obtener f(l) a partir de la hipótesis H en combinación
;;;                 con el elemento car(l) de la lista l que no forma parte
;;;                 del argumento de la hipótesis
;;;                 f(l) = combinar adecuadamente car(l) y H
;;;

;;;------------------------------------------------------------------------
;;; Ejemplo: Definir la función my-length(l), que retorna el número de
;;; elementos de la lista dada.
;
; 1. Base       : el resultado de la función para la lista vacía;
;                 my-length(()) = 0
; 2. Recurrencia: l no es la lista vacía; es decir, l = cons(car(l), cdr(l))
;      Hipótesis: se conoce my-length(cdr(l)) = H
;      Tesis    : my-lenhth(l) = H + 1
;
; En Racket:

(define (my-length l)
  (if [null? l]
      0
      (+ (my-length (cdr l)) 1)))

(displayln "my-length:")
(my-length '(a (b c) d))           ; => 3
(my-length '((a b c) d (e (f g)))) ; => 3

;;;------------------------------------------------------------------------
;;; CONSTRUIR LAS SIGUIENTES FUNCIONES RECURSIVAS
;;;------------------------------------------------------------------------
;;; En todos los casos es imprescindible realizar el análisis por casos.
;;; Estableciendo la base y la recurrencia de la misma forma que se ha
;;; hecho en el ejemplo previo.
;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Función my-reverse(l) que retorna una lista con los mismos elementos
; que la proporcionada, pero en orden inverso.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
; Base: Lista vacía, se retorna la lista vacía.
;
; Recurrencia: l no es vacía. l = (cons (car l) (cdr l)).
;   Hipótesis: se conoce (my-reverse (cdr l)) = H
;   Tésis    : (my-reverse l) = (cons H (car l))

(define (my-reverse l)
  (if (null? l)
      ()
      (append (my-reverse (cdr l)) (list (car l)))
      )
  )

(displayln "my-reverse:")
(my-reverse '((b (a)) c d))   ; => (d c (b (a)))
(my-reverse '(a (b c) d (e))) ; => ((e) d (b c) a)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Supuesto que los elementos de una lista representan un conjunto
; (recuérdese que un conjunto no hay orden y tampoco repeticiones)
; definir la función adjoin(x, A) que retorna un nuevo conjunto
; A + {x}
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
; 1. Base       : Lista vacía. Se retorna (list x)
;                 
; 2. Recurrencia: Lista no vacía. A = (cons (car A) (cdr A))
;      Hipótesis: Se conoce (adjoin x (cdr A)) = H
;      Tesis    : (adjoin x A) = si (eq? (car A) x) (H) (cons (car A) H)
;
; En Racket:

(define (adjoin x A)
  (cond [(null? A) (list x)]
        [(equal? (car A) x) (adjoin x (cdr A))]
        [else (cons (car A) (adjoin x (cdr A)))]
        )
  )
          
(displayln "adjoin:")
(adjoin '(a) '((b c) (d))) ; => ((b c) (d) (a))
(adjoin 0 '(5 0 7 10)) ; => (5 7 10 0)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Definir la función union(A,B) = A U B que retorna el conjunto
; unión de los dos dados.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
; 1. Base       : Lista A vacía. Se retorna B
;                 
; 2. Recurrencia: A no vacía. A = (cons (car A) (cdr A))
;      Hipótesis: Se conoce (union (cdr A) B) = H
;      Tesis    : (union A B) = (adjoin (car A) (union (cdr A) B))
;
; En Racket:

(define (unionA A B)     
  (if (null? A)
      B
      (adjoin (car A)
              (unionA B (cdr A)))
      )
  )

(define (union A B)
  (if (null? B)
      A
      (adjoin (car B)
              (union A (cdr B)))
      )
  )

(displayln "union:")
(union '((1) (2) (3)) '((2) (5))) ; => ((1) (3) (2) (5))
(union '(a f c b) '(z a b c))     ; => (f z a b c)

;;; Definición de funciones recursivas sobre S-expresiones
;;; ------------------------------------------------------
;;;
;;; En prácticas sólo se trabajará con átomos y el subconjunto de
;;; S-expresiones que son listas (listas anidadas o multinivel) y en
;;; el análisis por casos se utilizará la notación funcional estándar:
;;; <nombre_función>(arg0, arg1, ...). Por ejemplo, para f(Sexp):
;;;
;;; 1. Base       : el resultado de la función la función para un átomo;
;;;                 f(átomo) = lo que corresponda según problema a resolver
;;;                 Deberá analizarse el caso particular f(()), ya que la
;;;                 lista vacía también es un átomo.
;;; 2. Recurrencia: Sexp no es un átomo; es decir, Sexp=cons(car(Sexp), cdr(Sexp))
;;;      Hipótesis: se conocen f(car(Sexp))= H1 y f(cdr(Sexp)) = H2
;;;          Tesis: obtener f(Sexp) combinando ambas hipótesis, H1 y H2
;;;                 f(Sexp) = combinar adecuadamente H1 y H2
;;;

;;;------------------------------------------------------------------------
;;; Ejemplo: Definir la función atoms(Sexp), que retorna el número de
;;; átomos de la S-expresión dada.
;
; 1. Base       : el resultado de la función para un átomo;
;                 atoms(átomo) = 1; excepto si el átomo es la lista vacía
;                 atoms(()) = 0.
; 2. Recurrencia: Sexp no es un átomo; es decir, Sexp=cons(car(Sexp),cdr(Sexp))
;      Hipótesis: se conocen atoms(car(Sexp)) = H1 y atoms(cdr(Sexp)) = H2
;      Tesis    : atoms(Sexp) = H1 + H2
;
; En Racket:

(define (atoms Sexp)
  (cond [(null? Sexp) 0]
        [(atom? Sexp) 1]
        [else (+ (atoms (car Sexp))
                 (atoms (cdr Sexp)))]))

(displayln "atoms:")
(atoms '((b (c) a) d))         ; => 4
(atoms '((a b c) d (e (f g)))) ; => 7

;;;------------------------------------------------------------------------
;;; CONSTRUIR LAS SIGUIENTES FUNCIONES RECURSIVAS
;;;------------------------------------------------------------------------
;;; En todos los casos es imprescindible realizar el análisis por casos.
;;; Estableciendo la base y la recurrencia de la misma forma que se ha
;;; hecho en el ejemplo previo.
;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Definir la función ocurrencias(x, Sexp) que retorna el número de
; ocurrencias del átomo x en la S-expresión dada Sexp
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
; 1. Base       : Sexp es la lista vacía.
;                 
; 2. Recurrencia: Sexp no es un átomo, Sexp = (cons (car Sexp) (cdr Sexp))
;      Hipótesis: Se conoce (ocurrencias x (cdr Sexp)) = H
;      Tesis    : (ocurrencias x, Sexp) = (if (equal? x (car Sexp)) (+ H 1) (H))
;
; En Racket:

(define (ocurrencias x Sexp)
  (cond [(null? Sexp) 0]
        [(and (atom? Sexp)  (equal? x Sexp)) 1]
        [(not (atom? Sexp)) (+ (ocurrencias x (car Sexp))
                               (ocurrencias x (cdr Sexp))
                             )]
        [else 0]
        )
  )

(displayln "ocurrencias:")
(ocurrencias 'a '((a b (a)) (a) c))  ; => 3
(ocurrencias '1 '((1 2 (1)) 1 (0 1))) ; => 4

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Definir la función erase(x, Sexp) que retorna una S-expresión
; copia de Sexp, pero que no contiene ninguna ocurrencia de x
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
; 1. Base       : Sexp es la lista vacía. Se retorna '()
;                 
; 2. Recurrencia: Sexp no es la lista vacía. Sexp = (cons (car Sexp) (cdr Sexp))
;      Hipótesis: Se conoce: (erase x (cdr Sexp)) = H1 y (erase x (car Sexp)) = H2
;      Tesis    : (erase x Sexp) = (cons H2 H1)
;
; En Racket:
; FIXME: Caracter vacio?

(define (erase x Sexp)
  (cond [(null? Sexp) '()]
        [(equal? x Sexp) 0]
        [(not (atom? Sexp)) (cons (erase x (car Sexp))
                                  (erase x (cdr Sexp))
                             )]
        [else Sexp]
        )
  )

(displayln "erase:")

(erase '(f) '((a (b c) (f)) (d a (e)) f)) ; => ((a (b c)) (d a (e)) f)
(erase 'f '((a (b c) ()) (d a (e)) f))    ; => ((a (b c)) (d a (e)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Definir la función ocurrencias2(x, Sexp) que retorna el número de
; ocurrencias de la S-expresión x en la S-expresión Sexp
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;

(define (ocurrencias2 x Sexp)
  (cond [(null? Sexp) 0]
        [(equal? x Sexp) 1]
        [(not (atom? Sexp)) (+ (ocurrencias2 x (car Sexp))
                               (ocurrencias2 x (cdr Sexp))
                             )]
        [else 0]
        )
  )


(displayln "ocurrencias2:")
(ocurrencias2 'a '((a b (a)) (a) c))                 ; => 3
(ocurrencias2 '(a) '((a b (a)) (a) c))               ; => 2
(ocurrencias2 '(0 1) '((1 2 ((0 1))) ((0) 1) (0 1))) ; => 2

;;;------------------------------------------------------------------------
;;; EJERCICIOS COMPLEMENTARIOS
;;;------------------------------------------------------------------------
;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Definir la función cons-atoms(sexp) que retorna la S-expresión
; que permite obtener sexp a partir de sus átomos.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
; 1. Base       : Sexp está vacía. Se retorna ().
;                 Sexp es un átomo. Se retorna el átomo. 'Sexp = (list 'quote 'atomo)
;                 
; 2. Recurrencia: Sexp ni es lista vacía ni átomo.  Sexp = (cons (car Sexp) (cdr Sexp))
;      Hipótesis: Se conocen (cons-atoms (car Sexp)) = H1 y (cons-atoms (cdr sexp)) = H2
;      Tesis    : (cons-atoms sexp) = (cons (cons-atoms (car sexp)) (cons-atoms (cdr sexp)))
;
; En Racket:

(define (cons-atoms sexp)
  (cond [(null? sexp) ()]
        [(atom? sexp) (list 'quote sexp)]
        [else (list 'cons (cons-atoms (car sexp)) (cons-atoms (cdr sexp)))]
        )
  )
                 

(displayln "cons-atoms:")
(cons-atoms '(a b)) ;=> (cons 'a (cons 'b ()))
(cons-atoms '(a (b) ((c) d))) ;=> (cons 'a (cons (cons 'b ()) (cons (cons (cons 'c ()) (cons 'd ())) ())))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Definir la función (A,B) que retorna el conjunto intersección
; de los dos proporcionados.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
; Base: Lista A vacía ==> Se retorna '()
;
; Recurrencia: A no es vacía. A = (cons (car a) (cdr a))
; Hipótesis: Se conoce (intersection (cdr a) b) = H
; Tésis: (intersection a b) = (cons (if (not (zero? (ocurrencias2 (car a) b))) (car a)) H)
;
; FIXME: Caracter vacio?

(define (intersection a b)
  (cond [(null? a) '()]
        [else (cons (if (not (zero? (ocurrencias2 (car a) b))) (car a)
                        ())
                    (intersection (cdr a) b)
                    
                    )
              ]
        )
  )
                   

(displayln "intersection:")
(intersection '((1) (2) (3)) '((2) (5))) ; => ((2))
(intersection '(a f c b) '(z a b c))     ; => (a c b)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; existe?(x, Sexp) retorna cierto si la S-expresión x está
; contenida en la S-expresión Sexp 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; FIXME

(define (existe? x Sexp)
  (cond [(null? Sexp) #f]
        [(equal? x Sexp) #t]
        [(not (atom? Sexp)) (or (existe? x (car Sexp))
                               (existe? x (cdr Sexp))
                             )]
        [else #f]
        )
  )
(trace existe?)

(displayln "existe?")
(existe?  'a '((a b (a)) (e) c))     ;=> #t
(existe? '(a)  '((a b (a)) (e) c))   ;=> #t
(existe? '((a))  '((a b (a)) (e) c)) ;=> #f
