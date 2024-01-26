package request

type MemberRequest struct {
	Name     string `json:"name"`
	Password string `json:"password"`
	Address  string `json:"address"`
	Phone    string `json:"phone"`
}
