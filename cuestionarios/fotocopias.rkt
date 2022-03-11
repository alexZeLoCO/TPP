(require mzlib/compat racket/function)

;; EJEMPLO DE FUNCIONES EN SCHEME DEFINIDAS SOBRE UNA LISTA DE CONSUMOS DE
;; FOTOCOPIAS, EN B/N Y COLOR, DE USUARIOS (IDs) EN DISTINTAS SEDES DE UNA
;; EMPRESA.
;;
;; FORMATO DE LOS DATOS:
;; copias::(listof <consumo>)
;; <consumo>::(<sede> <id-usuario> <fotocopias-color> <fotocopias-b/n>)

(define copias '(
                 (Gijón 1 178 788)  (Oviedo 3 0 0) 
                 (Gijón 4 168 258) (Oviedo 1 651 296)
                 (Oviedo 2 0 0) (Avilés 2 0 0)
                 (Avilés 1 10 20) (Gijón 2 128 170)
                 (Avilés 3 0 0)
                 ))

;; Funciones de acceso a los campos (observadores):
(define sede car)
(define id cadr)
(define color caddr)
(define bn cadddr)

; 1
(define datos '((x 2.3 9.8 3.5 7.5 2.15 8.3)
                (y 3.5 3.6 9.75 9.4 3.45 7.2)
                (z 7.2 8.4 7.25 14.8 13.4 2.4)
                (u 8.3 2.3 7.6 8.4 9.2 3.45)
                (v 3.5 6.8 7.9 7.25 2.3 7.7)
                (w 3.25 9.8 2.3 10.2 9.75 9.4)))

(define (uno datos)
  (map (curry filter number?) datos)
  )

(displayln "Uno")
(uno datos)
(filter number? (apply append datos))

; 2
(define (dos datos)
  (map (lambda (dato) (list (car dato) (apply max (cdr dato)))) datos)
  )

(displayln "Dos")
(dos datos)

; 3 y 4
(define (foto-de sede consumos)
  (filter (curry member sede) consumos)
  )
(define (foto-de-corregido la-sede consumos)
  (filter (compose (curry eq? la-sede) sede) consumos))

(displayln "Cuatro")
(foto-de 'Gijón copias)
(foto-de-corregido 'Gijón copias)

; 5
; (sedes (cdr copias)) = H
; (sedes copias) = (if (not (member (caar copias) (cdr copias))) (cons (caar copias) H)
;                      H
(define (sedes copias)
  (cond [(empty? copias) '()]
        [(empty? (filter (curry member (caar copias)) (cdr copias))) (cons (caar copias) (sedes (cdr copias)))]
        [else (sedes (cdr copias))]
        )
  )

(define (sedes-corregido las-copias)
  (letrec ([solo-sedes (map sede las-copias)]
           [make-set (lambda(l)
                       (cond [(null? l) l]
                             [(member (car l) (cdr l)) (make-set (cdr l))]
                             [else (cons (car l) (make-set (cdr l)))]))])
    (make-set solo-sedes)))

(displayln "Cinco")
(sedes copias)
(sedes-corregido copias)

; 6
(define (copias-totales copias)
  (list (apply + (map bn copias)) (apply + (map color copias)))
  )

(displayln "Seis")
(copias-totales copias)

; 7
(define (totales-por-sede sede copias)
  (copias-totales (foto-de sede copias))
  )

(define (totales-por-sede-corregido la-sede las-copias)
  (let* ([copias-sede (filter (compose (curry eq? la-sede) sede) las-copias)]
         [totales (list (apply + (map color copias-sede)) (apply + (map bn copias-sede)))])
    totales))

(displayln "Siete")
(totales-por-sede 'Gijón copias)
(totales-por-sede-corregido 'Gijón copias)

; 8
(define (totales-en-cada-sede copias)
  (map (lambda (sede) (cons sede (totales-por-sede sede copias))) (sedes copias))
  )

; FIXME:
;(define (totales-en-cada-sede-corregido las-copias)
;  (let ([las-sedes (sedes las-copias)])
;    (map cons las-sedes 
;         (map (lambda(s) (totales-sede-corregido s copias))  las-sedes))))

(displayln "Ocho")
(totales-en-cada-sede copias)
;(totales-en-cada-sede-corregido copias)