//ENUM for use with abstract syntax trees
public enum K_op_type {
  K_cat, // concatenation
  K_alt, // alternation
  K_star, // Kleene star quantifier
  K_plus, // Kleene plus quantifier
  K_qmark, // question quantifier
  K_self, // exacty one implicit quantifier
  K_pos_set, //
  K_neg_set, // complement
  K_paren, // parentheses
  K_period, // match anything
  K_char,
  ERROR;
}
