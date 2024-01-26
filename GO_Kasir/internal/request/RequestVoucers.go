package request

type VoucersRequest struct {
	Discount  float64 `json:"discount"`
	StartDate string  `json:"start_date"`
	EndDate   string  `json:"end_date"`
	IsActive  string  `json:"is_active"`
}
