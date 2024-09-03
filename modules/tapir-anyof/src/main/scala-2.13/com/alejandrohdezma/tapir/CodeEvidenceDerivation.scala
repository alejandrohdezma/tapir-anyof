package com.alejandrohdezma.tapir

import shapeless.Annotation

private[tapir] trait CodeEvidenceDerivation {

  implicit def codeEvidence[A: Annotation[code, *]]: code.Evidence[A] =
    code.Evidence(Annotation[code, A].apply())

}
