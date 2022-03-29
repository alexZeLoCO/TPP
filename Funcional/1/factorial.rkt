(define n 3)
(define (factorial n)
  (if (zero? n) 1 (* n (factorial (- n 1)))))
