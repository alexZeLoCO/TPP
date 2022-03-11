(require mzlib/compat racket/function)

(define (suma-posiciones nums pos)
  (if (and (eq? (length nums) (length pos))
           (list? nums)
           (list? pos)
       )
      (oper-suma-posiciones nums pos)
      )
  )    

; Caso base: lista vacía ==> Retornar 0
; Caso recursivo:
; Conocido (oper-suma-posiciones (cdr nums) (cdr pos)) = H
; (oper-suma-posiciones nums pos) = H + (if (eq? (car pos) 1) (car nums)
;                                                             0)
(define (oper-suma-posiciones nums pos)
  (cond [(empty? nums) 0]
        [(eq? (car pos) 1) (+ (car nums) (oper-suma-posiciones (cdr nums) (cdr pos)))]
        [else                          (oper-suma-posiciones (cdr nums) (cdr pos))]
        )
  )

; Caso base: lista vacía ==> Retornar '()
; caso recursivo:
; Conocido (add-last x (cdr ll)) = H
; (add-last x ll) = (cons (append (car ll) (list x)) H)
(define (add-last x ll)
  (cond [(empty? ll) '()]
        [else (cons (append (car ll) (list x)) (add-last x (cdr ll)))]
        )
  )
      
; Caso base: elemento nulo             ==> Retornar -1
;            atomo igual a objetivo    ==> Retornar 0
;            atomo no igual a objetivo ==> Retornar ? Recubrir en otra foo?
; Caso recursivo:
; Conocido (maxNivelAtomo x (cdr sexp)) = H
; (maxNivelAtomo x sexp) = (max (+ 1 (maxNivelAtomo x (car sexp))) H)
(define (maxNivelAtomo x sexp)
  (cond [(null? sexp) -1]
        [(and (not (list? sexp))
              (equal? sexp x)) 0]
        [(not (list? sexp)) -1]
        [else (max (+ 1 (maxNivelAtomo x (car sexp))) (maxNivelAtomo x (cdr sexp)))]
        )
  )

; Caso base: lista vacía         ==> Retornar (list n)
;            (f-ord n (car ln))) ==> Retornar (cons n ln)
; Caso recursivo:
; Conocido (add n (cdr ln) f-ord) = H
; (add n ln f-ord) = (cons (car ln) (add n (cdr ln) f-ord))
(define (add n ln f-ord)
  (cond [(empty? ln) (list n)]
        [(f-ord n (car ln)) (cons n ln)]
        [else (cons (car ln) (add n (cdr ln) f-ord))]
        )
  )

; Caso base: lista vacía ==> Retornar '()
; Caso recursivo:
; Conocido (my-sort (cdr l) f-ord) = H
; (my-sort l f-ord) = (add (car l) H f-ord)
(define (my-sort l f-ord)
  (cond [(empty? l) '()]
        [else (add (car l) (my-sort (cdr l) f-ord) f-ord)]
        )
  )

; Caso base: ambas listas son vacías ==> Retornar #t
; Caso recursivo:
; Conocido (matching1? (cdr patron) (cdr sexp)) = H
; (matching1? patron sexp) = (and H (or (and (eq? (car patron) '*) (list? (car sexp)))
;                                       (and (eq? (car patron) '^) (atom? (car sexp)))
;                                        (eq? (car patron) (car sexp))
;                                        )
;                             )
; Excepto si el patrón es una lista. En ese caso se llama al matchging1? con ambos car.
(define (matching1? patron sexp)
  (cond [(and (empty? patron) (empty? sexp)) #t]
        [(empty? patron) #f]
        [(empty? sexp) #f]
        [(list? (car patron)) (matching1? (car patron) (car sexp))]
        [else (and (matching1? (cdr patron) (cdr sexp))
                   (or (and (equal? (car patron) '*) (list? (car sexp)))
                       (and (equal? (car patron) '^) (atom? (car sexp)))
                       (eq? (car patron) (car sexp))
                       )
                   )
              ]
        )
  )

; Caso base: patrón y sexpr vacíos ==> true
;            patrón vacío ==> false
;            sexp vacía ==> false
; Caso recursivo:
; Conocidos: Caso normal: (matching2? (cdr patron) (cdr sexp)) = H1
;            Caso '?    : (or (matching2? (cdr patron) sexp)
;                             (matching2? (cdr patron) (cdr sexp)) = H2
; (matching2? patron sexp) = (if (equal? (car sexp) '?) (or (and (atom? (car sexp)) (matching2? (cdr patron) (cdr sexp)))
;                                                           (matching2? (cdr patron) sexp)))
;                                                       (apartado anterior H1 ...)
(define (matching2? patron sexp)
  (cond [(and (empty? patron) (empty? sexp)) #t]
        [(empty? patron) #f]
        [(empty? sexp)  #f]
        [(list? (car patron)) (matching2? (car patron) (car sexp))]
        [(equal? (car patron) '?) (or (and (atom? (car sexp)) (matching2? (cdr patron) (cdr sexp)))
                                      (matching2? (cdr patron) sexp))]
        [else (and (matching2? (cdr patron) (cdr sexp))
                   (or (and (equal? (car patron) '*) (list? (car sexp)))
                       (and (equal? (car patron) '^) (atom? (car sexp)))
                       (eq? (car patron) (car sexp))
                       )
                   )
              ]
        )
  )