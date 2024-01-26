package response

type VoucerResponse struct {
	ID        uint   `json:"ID"`
	Code      string `json:"code"`
	Discount  string `json:"discount"`
	StartDate string `json:"start_date"`
	EndDate   string `json:"end_date"`
	IsActive  string `json:"is_active"`
}
