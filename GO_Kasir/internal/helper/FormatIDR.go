package helper

import (
	"strconv"
	"strings"
)

func FormatIDR(amount float64) string {
	amountStr := strconv.FormatFloat(amount, 'f', 0, 64)

	parts := strings.Split(amountStr, ".")

	intPart, _ := strconv.Atoi(parts[0])
	formattedIntPart := formatThousandSeparator(intPart)

	formattedAmount := formattedIntPart + "." + parts[1]

	formattedAmount = formattedAmount

	return formattedAmount
}

func formatThousandSeparator(n int) string {
	// Format bagian bulat dengan pemisah ribuan
	intPartStr := strconv.FormatInt(int64(n), 10)
	var formattedIntPart string

	for i := len(intPartStr); i > 0; i -= 3 {
		if i-3 > 0 {
			formattedIntPart = "." + intPartStr[i-3:i] + formattedIntPart
		} else {
			formattedIntPart = intPartStr[0:i] + formattedIntPart
		}
	}

	return formattedIntPart
}
