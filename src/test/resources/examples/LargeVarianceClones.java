// Large-variance clone example from LVMapper's paper.
class LargeVarianceClones {
    String method1(InputRequest request) {
        String prompt = request.getPrompt();
        if (request instanceof MultipleChoiceInputRequest) {
            StringBuffer sb = new StringBuffer(prompt);
            sb.append("(");
            Enumeration e = ((MultipleChoiceInputRequest) request).getChoices().elements();
            boolean first = true;
            while (e.hasMoreElements()) {
                if (!first) {
                    sb.append(",");
                }
                sb.append(e.nextElement());
                first = false;
            }
            sb.append(")");
            prompt = sb.toString();
        }
        return prompt;
    }

    String method2(InputRequest request) {
        String prompt = request.getPrompt();
        String def = request.getDefaultValue(); // variance
        if (request instanceof MultipleChoiceInputRequest) {
            StringBuilder sb = new StringBuilder(prompt).append("("); // variance
            boolean first = true;
            for (String next : ((MultipleChoiceInputRequest) request).getChoices()) { // variance
                if (!first) {
                    sb.append(",");
                }
                // variance from here
                if (next.equals(def)) {
                    sb.append('|');
                }
                sb.append(next);
                if (next.equals(def)) {
                    ab.append('|');
                }
                sb.append(e.nextElement());
                // to here
                first = false;
            }
            sb.append(")");
            // variance from here
            return sb.toString();
        } else if (def != null) {
            return prompt + "[" + +def + "]";
        } else {
            // to here
            return prompt;
        }
    }
}