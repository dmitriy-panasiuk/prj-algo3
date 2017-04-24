(load "~/Documents/algo/prj-algo3/scripts/graphs.lisp")
(load "~/Documents/algo/prj-algo3/tasks/graph.lisp")

(defun edges-of (graph)
  (let (edges)
    (maphash (lambda (name node)
               (dolist (edge (edges node))
                 (push edge edges)))
             (nodes graph))
    (reverse edges)))
(print (edges-of *example-graph*))

(defun bellman-ford (graph v)
  (let* ((n (ht-count (nodes graph)))
        (res (make-array (+ 1 n) :initial-element most-positive-fixnum))
        (edges (edges-of graph)))
    (setf (aref res v) 0)
    (dotimes(i n)
      (let ((changed nil))
        (dolist (edge edges)
                (with ((a (id (src edge)))
                       (b (id (dst edge)))
                       (newval (min (+ (aref res a) (label edge)) most-positive-fixnum)))
                  (if (and (< newval most-positive-fixnum) (> (aref res b) newval))
                    (setf (aref res b) newval)
                    (setf changed t))
                  ))
        (unless changed (return-from bellman-ford res))))
    res)
)

(print (bellman-ford *example-graph* 0))