package response

type ResponseAuthSuccess struct {
	Status string `json:"status"`
	Token  string `json:"token"`
}
type ResponseDataSuccess struct {
	Status string `json:"status"`
	Data   string `json:"data"`
}

type ResponseError struct {
	Status  string `json:"status"`
	Message string `json:"message"`
}
